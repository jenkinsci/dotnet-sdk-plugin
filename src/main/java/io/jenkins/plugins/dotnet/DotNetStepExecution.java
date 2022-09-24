package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Functions;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import io.jenkins.plugins.dotnet.console.DiagnosticScanner;
import io.jenkins.plugins.dotnet.extensions.commands.Command;
import io.jenkins.plugins.dotnet.extensions.commands.CommandLineArgumentProvider;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;

public class DotNetStepExecution extends StepExecution {

  private static final long serialVersionUID = 6163911949142462653L;

  @NonNull
  private final Command command;

  protected DotNetStepExecution(@NonNull StepContext context, @NonNull Command command, @NonNull Settings settings) {
    super(context);
    this.command = command;
    this.settings = settings;
  }

  public final static class Settings implements Serializable {

    private static final long serialVersionUID = 1230600125426299437L;

    /** A specific charset to use for the command's output. If {@code null}, the build's default charset will be used. */
    @CheckForNull
    public String charset = null;

    /** Indicates whether diagnostic output should be enabled. */
    public boolean diagnostics = false;

    /** Indicates whether project execution should continue when there is an error in this step. */
    public boolean continueOnError = false;

    /** Indicates whether a specific SDK version should be used. */
    public boolean specificSdkVersion = false;

    /** The name of the SDK to use. */
    @CheckForNull
    public String sdk = null;

    /** Indicates whether the presence of errors makes the build unstable (instead of failed). */
    public boolean unstableIfErrors = false;

    /** Indicates whether the presence of warnings makes the build unstable (instead of successful). */
    public boolean unstableIfWarnings = false;

    /** The working directory to use for the command. This directory is <em>not</em> created by the command execution. */
    @CheckForNull
    public String workDirectory = null;

    //region Compatibility Options

    // These options are here only to preserve behaviour of the old commands. These should be removed when those are dropped.

    /** Indicates whether SDK information should be shown. */
    public boolean showSdkInfo = false;

    /** Indicates whether any build servers started by the main command should be shut down. */
    public boolean shutDownBuildServers = false;

    //endregion

  }

  @NonNull
  private final Settings settings;

  @CheckForNull
  @Override
  public String getStatus() {
    // TODO: Maybe report something like "running '<command line>'"
    return null;
  }

  public static int perform(@NonNull CommandLineArgumentProvider command, @NonNull Settings settings, @NonNull Run<?, ?> run,
                            @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher,
                            @NonNull TaskListener listener) throws InterruptedException, IOException {
    final DotNetSDK sdkInstance;
    if (settings.sdk == null) {
      sdkInstance = null;
    }
    else {
      final DotNetSDK.DescriptorImpl desc = ToolInstallation.all().get(DotNetSDK.DescriptorImpl.class);
      if (desc == null) {
        throw new AbortException(".NET SDK descriptor not found.");
      }
      sdkInstance = desc.prepareAndValidateInstance(settings.sdk, workspace, env, listener);
    }
    final String executable;
    if (sdkInstance != null) {
      executable = sdkInstance.ensureExecutableExists(launcher);
      sdkInstance.buildEnvVars(env);
    }
    else {
      final String basename = DotNetSDK.getExecutableFileName(launcher);
      {
        final String home = Util.fixEmptyAndTrim(env.get(DotNetSDK.ROOT_ENVIRONMENT_VARIABLE, ""));
        if (home != null) {
          // construct the full remote path
          executable = new FilePath(launcher.getChannel(), home).child(basename).absolutize().getRemote();
        }
        else {
          // rely on the system's PATH
          executable = basename;
        }
      }
    }
    if (settings.workDirectory != null) {
      workspace = workspace.child(settings.workDirectory);
    }
    try {
      if (sdkInstance != null && settings.specificSdkVersion) {
        sdkInstance.createGlobalJson(workspace, listener);
      }
      final Charset cs = settings.charset == null ? run.getCharset() : Charset.forName(settings.charset);
      try (final DiagnosticScanner scanner = new DiagnosticScanner(listener.getLogger(), cs, false)) {
        if (settings.showSdkInfo) {
          final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable);
          final DotNetArguments arguments = new DotNetArguments(run, cmdLine);
          if (settings.diagnostics) {
            arguments.addFlag("diagnostics");
          }
          arguments.addFlag("info");
          launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(workspace).join();
        }
        int rc = -1;
        {
          final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable);
          final DotNetArguments arguments = new DotNetArguments(run, cmdLine);
          if (settings.diagnostics) {
            arguments.addFlag("diagnostics");
          }
          command.addCommandLineArguments(arguments);
          try {
            rc = launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(workspace).join();
          }
          finally {
            scanner.writeCompletionMessage(rc);
          }
        }
        if (settings.shutDownBuildServers) {
          final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable);
          final DotNetArguments arguments = new DotNetArguments(run, cmdLine);
          if (settings.diagnostics) {
            arguments.addFlag("diagnostics");
          }
          arguments.add("build-server", "shutdown");
          launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(workspace).join();
        }
        final int errors = scanner.getErrors();
        if (errors > 0) {
          if (settings.unstableIfErrors) {
            run.setResult(Result.UNSTABLE);
          }
          else if (settings.continueOnError) {
            run.setResult(Result.FAILURE);
          }
          else {
            throw new AbortException(Messages.DotNetStepExecution_CompletedWithErrors(errors));
          }
        }
        else if (rc != 0) {
          if (settings.continueOnError) {
            run.setResult(Result.FAILURE);
          }
          else {
            throw new AbortException(Messages.DotNetStepExecution_CompletedWithNonZeroReturnCode(rc));
          }
        }
        else if (settings.unstableIfWarnings && scanner.getWarnings() > 0) {
          run.setResult(Result.UNSTABLE);
        }
        return rc;
      }
    }
    catch (AbortException ae) {
      throw ae;
    }
    catch (Throwable t) {
      Functions.printStackTrace(t, listener.fatalError(Messages.DotNetStepExecution_Failed()));
      throw new AbortException(Messages.DotNetStepExecution_Failed());
    }
    finally {
      if (sdkInstance != null && settings.specificSdkVersion) {
        DotNetSDK.removeGlobalJson(workspace, listener);
      }
    }
  }

  @Override
  public boolean start() throws Exception {
    final StepContext ctx = this.getContext();
    final Run<?, ?> run = Objects.requireNonNull(ctx.get(Run.class));
    final FilePath workspace = Objects.requireNonNull(ctx.get(FilePath.class));
    final EnvVars env = Objects.requireNonNull(ctx.get(EnvVars.class));
    final Launcher launcher = Objects.requireNonNull(ctx.get(Launcher.class));
    final TaskListener listener = Objects.requireNonNull(ctx.get(TaskListener.class));
    ctx.onSuccess(DotNetStepExecution.perform(this.command, this.settings, run, workspace, env, launcher, listener));
    return true;
  }

}
