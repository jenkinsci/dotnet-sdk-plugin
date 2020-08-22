package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Functions;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import io.jenkins.plugins.dotnet.DotNetSDK;
import io.jenkins.plugins.dotnet.console.DiagnosticScanner;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/** A build step executing a .NET CLI command. */
public class Command extends Builder {

  /** {@inheritDoc} */
  @Override
  public CommandDescriptor getDescriptor() {
    return (CommandDescriptor) super.getDescriptor();
  }

  /**
   * Adds command line arguments for this invocation of the {@code dotnet} CLI.
   *
   * @param args The current set of arguments.
   *
   * @throws AbortException When something goes wrong.
   */
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    // nothing to add at this level
  }

  /**
   * Gets the descriptor for a .NET SDK.
   *
   * @return The descriptor for a .NET SDK.
   *
   * @throws AbortException When the descriptor could not be found.
   */
  @NonNull
  public static DotNetSDK.DescriptorImpl getSdkDescriptor() throws AbortException {
    // spotbugs does not like explicit throws of NullPointerException (see https://github.com/spotbugs/spotbugs/issues/1175), so we
    // need to do the test explicitly instead of using Objects.requireNonNull.
    final DotNetSDK.DescriptorImpl sdkDescriptor = ToolInstallation.all().get(DotNetSDK.DescriptorImpl.class);
    if (sdkDescriptor == null)
      throw new AbortException(".NET SDK descriptor not found.");
    return sdkDescriptor;
  }

  /** {@inheritDoc} */
  @Override
  public boolean perform(@NonNull AbstractBuild<?, ?> build, @NonNull Launcher launcher, @NonNull BuildListener listener) throws InterruptedException, IOException {
    final FilePath workspace = build.getWorkspace();
    if (workspace == null)
      throw new AbortException(Messages.Command_NoWorkspace());
    final EnvVars env = build.getEnvironment(listener);
    for (Map.Entry<String, String> e : build.getBuildVariables().entrySet())
      env.put(e.getKey(), e.getValue());
    final Result r = this.run(build, workspace, env, launcher, listener, build.getCharset());
    if (r != Result.SUCCESS)
      build.setResult(r);
    return true;
  }

  /**
   * Runs this .NET command.
   *
   * @param run      The run context for the command.
   * @param wd       The base working directory for the command.
   * @param env      The environment variables that apply for the command.
   * @param launcher The launcher to use to execute the command.
   * @param listener The listener to report command output to.
   * @param cs       The character set to use for command output.
   *
   * @return The command's result.
   * @throws InterruptedException When execution is interrupted.
   * @throws IOException          When an I/O error occurs.
   */
  @NonNull
  public Result run(@NonNull Run<?, ?> run, @NonNull FilePath wd, @NonNull EnvVars env, @NonNull Launcher launcher, @NonNull TaskListener listener, @NonNull Charset cs) throws InterruptedException, IOException {
    final DotNetSDK sdkInstance;
    if (this.sdk == null)
      sdkInstance = null;
    else
      sdkInstance = Command.getSdkDescriptor().prepareAndValidateInstance(this.sdk, wd, env, listener);
    final String executable;
    if (sdkInstance != null) {
      executable = sdkInstance.ensureExecutableExists(launcher);
      sdkInstance.buildEnvVars(env);
    }
    else {
      final String basename = DotNetSDK.getExecutableFileName(launcher);
      {
        final String home = Util.fixEmptyAndTrim(env.get(DotNetSDK.HOME_ENVIRONMENT_VARIABLE, ""));
        if (home != null) // construct the full remote path
          executable = new FilePath(launcher.getChannel(), home).child(basename).absolutize().getRemote();
        else // will have to rely on the system's PATH
          executable = basename;
      }
    }
    if (this.workDirectory != null)
      wd = wd.child(this.workDirectory);
    try {
      if (sdkInstance != null && this.specificSdkVersion)
        sdkInstance.createGlobalJson(wd, listener);
      // Note: this MUST NOT BE CLOSED, because that also closes the build listener, causing all further output to go bye-bye
      final DiagnosticScanner scanner = new DiagnosticScanner(listener.getLogger(), cs);
      if (this.showSdkInfo) {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable, "--info");
        launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
      }
      int rc = -1;
      {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable);
        this.addCommandLineArguments(new DotNetArguments(run, cmdLine));
        try {
          rc = launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
        }
        finally {
          scanner.writeCompletionMessage(rc);
        }
      }
      if (this.shutDownBuildServers) {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable, "build-server", "shutdown");
        launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
      }
      // TODO: Maybe also add configuration to set the build as either failed or unstable based on return code
      if (rc != 0)
        return Result.FAILURE;
      if (scanner.getErrors() > 0)
        return Result.FAILURE;
      if (this.unstableIfWarnings && scanner.getWarnings() > 0)
        return Result.UNSTABLE;
      return Result.SUCCESS;
    }
    catch (Throwable t) {
      Functions.printStackTrace(t, listener.fatalError(Messages.Command_ExecutionFailed()));
      throw new AbortException(Messages.Command_ExecutionFailed());
    }
    finally {
      if (sdkInstance != null && this.specificSdkVersion)
        DotNetSDK.removeGlobalJson(wd, listener);
    }
  }

  //region Properties

  /** The name of the SDK to use. */
  @CheckForNull
  protected String sdk;

  /**
   * Gets the name of the SDK to use.
   *
   * @return The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @CheckForNull
  public String getSdk() {
    return this.sdk;
  }

  /**
   * Sets the name of the SDK to use.
   *
   * @param sdk The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @DataBoundSetter
  public void setSdk(@CheckForNull String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  /** Flag indicating whether or not SDK information should be shown. */
  protected boolean showSdkInfo = false;

  /**
   * Determines whether or not SDK information should be shown.
   *
   * @return {@code true} if "{@code dotnet --info}" should be run before the main command; {@code false} otherwise.
   */
  public boolean isShowSdkInfo() {
    return this.showSdkInfo;
  }

  /**
   * Determines whether or not SDK information should be shown.
   *
   * @param showSdkInfo {@code true} if "{@code dotnet --info}" should be run before the main command; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setShowSdkInfo(boolean showSdkInfo) {
    this.showSdkInfo = showSdkInfo;
  }

  /** Flag indicating whether or not any build servers started by the main command should be shut down. */
  protected boolean shutDownBuildServers = false;

  /** Flag indicating whether or not a specific SDK version should be used. */
  private boolean specificSdkVersion = false;

  /**
   * Determines whether or not a specific SDK version should be used.
   *
   * @return {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK (as opposed to
   * a more recent one that happens to be installed on the build agent); {@code false} otherwise.
   */
  public boolean isSpecificSdkVersion() {
    return this.specificSdkVersion;
  }

  /**
   * Determines whether or not a specific SDK version should be used.
   *
   * @param specificSdkVersion {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK
   *                           (as opposed to a more recent one that happens to be installed on the build agent); {@code false}
   *                           otherwise.
   */
  @DataBoundSetter
  public void setSpecificSdkVersion(boolean specificSdkVersion) {
    this.specificSdkVersion = specificSdkVersion;
  }

  /** Flag indicating whether or not the presence of warnings makes the build unstable. */
  protected boolean unstableIfWarnings = false;

  /** The working directory to use for the command. This directory is <em>not</em> created by the command execution. */
  @CheckForNull
  protected String workDirectory = null;

  /**
   * Gets the working directory to use for the command.
   *
   * @return The working directory to use for the command.
   */
  @CheckForNull
  public String getWorkDirectory() {
    return this.workDirectory;
  }

  /**
   * Sets the working directory to use for the command.
   *
   * @param workDirectory The working directory to use for the command.
   */
  @DataBoundSetter
  public void setWorkDirectory(@CheckForNull String workDirectory) {
    this.workDirectory = Util.fixEmpty(workDirectory);
  }

  //endregion

}
