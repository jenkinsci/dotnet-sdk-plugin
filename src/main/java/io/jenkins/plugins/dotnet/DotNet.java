package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.*;
import hudson.remoting.ChannelClosedException;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.FormValidation;
import hudson.util.LineEndingConversion;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.console.CompletionNote;
import io.jenkins.plugins.dotnet.console.DiagnosticScanner;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A builder using a .NET SDK. */
public abstract class DotNet extends Builder implements SimpleBuildStep {

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  private String quoteCommandForBatchFile(@NonNull List<String> command) {
    // TODO: Actual Quoting (FIXME: Or should we leave things as-is?)
    return String.join(" ", command);
  }

  private void addCommandToBatchFile(@NonNull List<String> command, @NonNull StringBuilder batchFile) {
    final String quotedCommand = this.quoteCommandForBatchFile(command);
    // FIXME: Ideally, the 'Running: ' and 'Command Exit Code: ' would be localizable, but that would allow an unscrupulous
    // FIXME: translator to translate 'Running: ' as 'Say goodbye to your data & format C:'.
    // FIXME: So until I have a suitable method to escape the string, they stay as they are.
    batchFile.append("echo ").append("Running: ").append(quotedCommand).append('\n');
    batchFile.append(quotedCommand).append('\n');
    batchFile.append("echo ").append("Command Exit Code: ").append("%ERRORLEVEL%\n");
  }

  private String createBatchFileContents(@CheckForNull List<String> preCommand, @NonNull List<String> mainCommand, @CheckForNull List<String> postCommand) {
    final StringBuilder sb = new StringBuilder();
    sb.append("@echo off\n");
    if (preCommand != null)
      this.addCommandToBatchFile(preCommand, sb);
    this.addCommandToBatchFile(mainCommand, sb);
    if (postCommand != null) {
      sb.append("set __jenkins_build_rc=%ERRORLEVEL%\n");
      this.addCommandToBatchFile(postCommand, sb);
      sb.append("exit %__jenkins_build_rc%\n");
    }
    else
      sb.append("exit %ERRORLEVEL%\n");
    return LineEndingConversion.convertEOL(sb.toString(), LineEndingConversion.EOLType.Windows);
  }

  private String quoteCommandForShellScript(@NonNull List<String> command) {
    // TODO: Actual Quoting (FIXME: Or should we leave things as-is?)
    return String.join(" ", command);
  }

  private String createShellScriptContents(@CheckForNull List<String> preCommand, @NonNull List<String> mainCommand, @CheckForNull List<String> postCommand) {
    final StringBuilder sb = new StringBuilder();
    sb.append("#!/bin/sh\n");
    if (preCommand != null)
      sb.append(this.quoteCommandForShellScript(preCommand)).append('\n');
    sb.append(this.quoteCommandForShellScript(mainCommand)).append('\n');
    if (postCommand != null) {
      sb.append("__jenkins_build_rc=$?\n");
      sb.append(this.quoteCommandForShellScript(postCommand)).append('\n');
      sb.append("exit $__jenkins_build_rc\n");
    }
    else
      sb.append("exit $?\n");
    return LineEndingConversion.convertEOL(sb.toString(), LineEndingConversion.EOLType.Unix);
  }

  protected abstract void addCommandLineArguments(@NonNull List<String> args);

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    final FilePath workspace = build.getWorkspace();
    if (workspace == null)
      throw new AbortException(Messages.DotNet_NoWorkspace());
    final EnvVars env = build.getEnvironment(listener);
    for (Map.Entry<String, String> e : build.getBuildVariables().entrySet())
      env.put(e.getKey(), e.getValue());
    final Result r = this.run(workspace, env, launcher, listener, build.getCharset());
    if (r != Result.SUCCESS)
      build.setResult(r);
    return true;
  }

  @Override
  public void perform(@NonNull Run<?, ?> run, @NonNull FilePath wd, @NonNull Launcher launcher, @NonNull TaskListener listener) throws InterruptedException, IOException {
    final Result r = this.run(wd, run.getEnvironment(listener), launcher, listener, run.getCharset());
    if (r != Result.SUCCESS)
      run.setResult(r);
  }

  private Result run(@NonNull FilePath wd, @NonNull EnvVars env, @NonNull Launcher launcher, @NonNull TaskListener listener, @NonNull Charset cs) throws InterruptedException, IOException {
    final DotNetSDK sdkInstance;
    if (this.sdk == null)
      sdkInstance = null;
    else
      sdkInstance = this.getDescriptor().getToolDescriptor().prepareAndValidateInstance(this.sdk, wd, env, listener);
    final String executable;
    if (sdkInstance != null)
      executable = sdkInstance.ensureExecutableExists(launcher);
    else // rely on the system path
      executable = DotNetSDK.getExecutableFileName(launcher);
    final List<String> mainCommand = new ArrayList<>();
    mainCommand.add(executable);
    this.addCommandLineArguments(mainCommand);
    final FilePath workDir;
    if (this.workDirectory == null)
      workDir = wd;
    else
      workDir = wd.child(this.workDirectory);
    final List<String> preCommand;
    if (this.showSdkInfo)
      preCommand = Arrays.asList(executable, "--info");
    else
      preCommand = null;
    final List<String> postCommand;
    if (this.shutDownBuildServers)
      postCommand = Arrays.asList(executable, "build-server", "shutdown");
    else
      postCommand = null;
    final FilePath script;
    final List<String> cmdLine = new ArrayList<>();
    try {
      final String extension;
      final String contents;
      if (launcher.isUnix()) {
        cmdLine.add("/bin/sh");
        cmdLine.add("-xe");
        contents = this.createShellScriptContents(preCommand, mainCommand, postCommand);
        extension = ".sh";
      }
      else {
        cmdLine.add("cmd.exe");
        cmdLine.add("/c");
        cmdLine.add("call");
        contents = this.createBatchFileContents(preCommand, mainCommand, postCommand);
        extension = ".cmd";
      }
      script = workDir.createTextTempFile("jenkins-dotnet-", extension, contents, false);
      cmdLine.add(script.getRemote());
    }
    catch (IOException e) {
      Util.displayIOException(e, listener);
      Functions.printStackTrace(e, listener.fatalError(Messages.DotNet_ScriptCreationFailed()));
      return Result.FAILURE;
    }
    int rc = -1;
    try {
      if (sdkInstance != null) {
        sdkInstance.buildEnvVars(env);
        if (this.specificSdkVersion)
          sdkInstance.createGlobalJson(workDir, launcher, listener);
      }
      // Note: this MUST NOT BE CLOSED, because that also closes the build listener, causing all further output to go bye-bye
      final DiagnosticScanner scanner = new DiagnosticScanner(listener.getLogger(), cs);
      try {
        rc = launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(workDir).join();
      }
      finally {
        scanner.forceEol();
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
    catch (IOException e) {
      Util.displayIOException(e, listener);
      Functions.printStackTrace(e, listener.fatalError(Messages.DotNet_ExecutionFailed()));
      throw new AbortException(Messages.DotNet_ExecutionFailed());
    }
    finally {
      try {
        final PrintStream out = listener.getLogger();
        out.print(Messages.DotNet_CommandCompleted());
        // properties don't do trailing blanks, so this needs to be hardcoded text
        out.print(" - ");
        // by encoding it here, we don't need matching to decide how to style the exit code
        new CompletionNote().encodeTo(out);
        out.println(Messages.DotNet_CommandExitCode(rc));
      }
      catch (Throwable t) {
        DotNet.LOGGER.log(Level.FINE, Messages.DotNet_CompletionMessageFailed(), t);
        // the annotator won't stop, but an error serious enough to make that output line fail is going to abort the build anyway
      }
      // FIXME: Perhaps this should be done more nicely - maybe using an AutoCloseable?
      if (sdkInstance != null && this.specificSdkVersion)
        sdkInstance.removeGlobalJson(workDir);
      try {
        script.delete();
      }
      catch (IOException e) {
        // Copied from CommandInterpreter. May or may not be particularly relevant.
        // This avoids reporting a big error immediately after reporting another, more relevant, error. (JENKINS-5073)
        if (rc == -1 && e.getCause() instanceof ChannelClosedException)
          DotNet.LOGGER.log(Level.FINE, Messages.DotNet_ScriptDeletionFailed(script), e);
        else {
          Util.displayIOException(e,listener);
          Functions.printStackTrace(e, listener.fatalError(Messages.DotNet_ScriptDeletionFailed(script)));
        }
      }
      catch (Exception e) {
        Functions.printStackTrace(e, listener.fatalError(Messages.DotNet_ScriptDeletionFailed(script)));
      }
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DotNet.class.getName());

  //region Properties

  protected String sdk;

  public String getSdk() {
    return this.sdk;
  }

  @DataBoundSetter
  public void setSdk(@CheckForNull String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  protected boolean showSdkInfo = false;

  public boolean isShowSdkInfo() {
    return this.showSdkInfo;
  }

  @DataBoundSetter
  public void setShowSdkInfo(boolean showSdkInfo) {
    this.showSdkInfo = showSdkInfo;
  }

  protected boolean shutDownBuildServers = false;

  private boolean specificSdkVersion = true;

  public boolean isSpecificSdkVersion() {
    return this.specificSdkVersion;
  }

  @DataBoundSetter
  public void setSpecificSdkVersion(boolean specificSdkVersion) {
    this.specificSdkVersion = specificSdkVersion;
  }

  protected boolean unstableIfWarnings = false;

  protected String workDirectory = null;

  public String getWorkDirectory() {
    return this.workDirectory;
  }

  @DataBoundSetter
  public void setWorkDirectory(@CheckForNull String workDirectory) {
    this.workDirectory = Util.fixEmpty(workDirectory);
  }

  //endregion

  //region DescriptorImpl

  public static abstract class DescriptorImpl extends BuildStepDescriptor<Builder> {

    protected DescriptorImpl() {
    }

    protected DescriptorImpl(Class<? extends DotNet> clazz) {
      super(clazz);
    }

    @SuppressWarnings("unused")
    public final AutoCompletionCandidates doAutoCompleteFramework(@QueryParameter String value) {
      return Framework.autoCompleteMoniker(value);
    }

    @SuppressWarnings("unused")
    public final AutoCompletionCandidates doAutoCompleteRuntime(@QueryParameter String value) {
      return Runtime.autoComplete(value);
    }

    @SuppressWarnings("unused")
    public final AutoCompletionCandidates doAutoCompleteRuntimes(@QueryParameter String value) {
      // FIXME: How to handle autocompletion of a space-separated list?
      return Runtime.autoComplete(value);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckFramework(@QueryParameter String value) {
      return Framework.checkMoniker(value);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckRuntime(@QueryParameter String value) {
      return Runtime.checkIdentifier(value);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckRuntimes(@QueryParameter String value) {
      return Runtime.checkIdentifiers(value);
    }

    @SuppressWarnings("unused")
    public final ListBoxModel doFillSdkItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_DefaultSDK(), "");
      DotNetSDK.addSdks(model);
      return model;
    }

    @SuppressWarnings("unused")
    public final ListBoxModel doFillVerbosityItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_Verbosity_Default(), "");
      model.add(Messages.DotNet_Verbosity_Quiet(), "q");
      model.add(Messages.DotNet_Verbosity_Minimal(), "m");
      model.add(Messages.DotNet_Verbosity_Normal(), "n");
      model.add(Messages.DotNet_Verbosity_Detailed(), "d");
      model.add(Messages.DotNet_Verbosity_Diagnostic(), "diag");
      return model;
    }

    @SuppressWarnings("unused")
    public final String getMoreOptions() {
      return Messages.DotNet_MoreOptions();
    }

    public final DotNetSDK.DescriptorImpl getToolDescriptor() {
      return ToolInstallation.all().get(DotNetSDK.DescriptorImpl.class);
    }

    public final boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

  }

  //endregion

}
