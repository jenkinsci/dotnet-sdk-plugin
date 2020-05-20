package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.remoting.ChannelClosedException;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ComboBoxModel;
import hudson.util.LineEndingConversion;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A builder using a .NET SDK. */
public abstract class DotNet extends Builder {

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
    batchFile.append("echo Running: ").append(quotedCommand).append('\n');
    batchFile.append(quotedCommand).append('\n');
    batchFile.append("echo Command Exit Code: %ERRORLEVEL%\n");
  }

  private String createBatchFileContents(@Nullable List<String> preCommand, @NonNull List<String> mainCommand, @Nullable List<String> postCommand) {
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

  private String createShellScriptContents(@Nullable List<String> preCommand, @NonNull List<String> mainCommand, @Nullable List<String> postCommand) {
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
    final DotNetSDK sdkInstance;
    if (this.sdk == null)
      sdkInstance = null;
    else
      sdkInstance = this.getDescriptor().getToolDescriptor().prepareAndValidateInstance(this.sdk, workspace, env, listener);
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
      workDir = workspace;
    else
      workDir = workspace.child(this.workDirectory);
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
    final DotNetConsoleProcessor dncp = new DotNetConsoleProcessor(listener.getLogger(), Charset.defaultCharset());
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
      return false;
    }
    int rc = -1;
    try {
      for (Map.Entry<String, String> e : build.getBuildVariables().entrySet())
        env.put(e.getKey(), e.getValue());
      if (sdkInstance != null)
        sdkInstance.buildEnvVars(env);
      rc = launcher.launch().cmds(cmdLine).envs(env).stdout(dncp).pwd(workDir).join();
      listener.getLogger().printf("Exit Code: %d%n", rc);
      // TODO: Maybe also add configuration to set the build as either failed or unstable based on return code
      if (rc != 0)
        return false;
      if (dncp.getErrors() > 0)
        return false;
      if (this.unstableIfWarnings && dncp.getWarnings() > 0)
        build.setResult(Result.UNSTABLE);
      return true;
    }
    catch (IOException e) {
      Util.displayIOException(e, listener);
      Functions.printStackTrace(e, listener.fatalError(Messages.DotNet_ExecutionFailed()));
      String msg = Messages.DotNet_ExecutionFailed();
      if (dncp.isCommandNotFound() && sdkInstance == null) {
        if (DotNetSDK.hasConfiguration()) // at least one SDK configured
          msg += Messages.DotNet_ToolConfigurationNeeded();
        else
          msg += Messages.DotNet_ToolSelectionNeeded();
      }
      throw new AbortException(msg);
    }
    finally {
      try {
        script.delete();
      }
      catch (IOException e) {
        // Copied from CommandInterpreter. May or may not be particularly relevant.
        // This avoids reporting a big error immediately after reporting another, more relevant, error. (JENKINS-5073)
        if (rc == -1 && e.getCause() instanceof ChannelClosedException)
          LOGGER.log(Level.FINE, Messages.DotNet_ScriptDeletionFailed(script), e);
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

  //region Properties

  // TODO: Add a "force exact version" flag; if set, the setup will create/update global.json in the workspace root to specify
  // TODO: the exact SDK version (to be provided by DotNetSDK). Otherwise, on Windows running 'dotnet' from .NET Core 2 and up will
  // TODO: use the highest version available, looking at both the SDK installed by Jenkins and the system-wide installed SDKs.
  // TODO: https://docs.microsoft.com/en-us/dotnet/core/versions/selection#the-sdk-uses-the-latest-installed-version

  protected String sdk;

  public String getSdk() {
    return this.sdk;
  }

  @DataBoundSetter
  public void setSdk(String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  protected String workDirectory = null;

  public String getWorkDirectory() {
    return this.workDirectory;
  }

  @DataBoundSetter
  public void setWorkDirectory(@Nullable String workDirectory) {
    this.workDirectory = workDirectory;
  }

  protected boolean shutDownBuildServers = false;

  public boolean isShutDownBuildServers() {
    return this.shutDownBuildServers;
  }

  @DataBoundSetter
  public void setShutDownBuildServers(boolean shutDownBuildServers) {
    this.shutDownBuildServers = shutDownBuildServers;
  }

  protected boolean unstableIfWarnings = false;

  public boolean isUnstableIfWarnings() {
    return this.unstableIfWarnings;
  }

  @DataBoundSetter
  public void setUnstableIfWarnings(boolean unstableIfWarnings) {
    this.unstableIfWarnings = unstableIfWarnings;
  }

  protected boolean showSdkInfo = true;

  public boolean isShowSdkInfo() {
    return this.showSdkInfo;
  }

  @DataBoundSetter
  public void setShowSdkInfo(boolean showSdkInfo) {
    this.showSdkInfo = showSdkInfo;
  }

  //endregion

  private static final Logger LOGGER = Logger.getLogger(DotNet.class.getName());

  //region DescriptorImpl

  public static abstract class DescriptorImpl extends BuildStepDescriptor<Builder> {

    protected DescriptorImpl() {
    }

    protected DescriptorImpl(Class<? extends DotNet> clazz) {
      super(clazz);
    }

    public final DotNetSDK.DescriptorImpl getToolDescriptor() {
      return ToolInstallation.all().get(DotNetSDK.DescriptorImpl.class);
    }

    public final ComboBoxModel doFillConfigurationItems() {
      final ComboBoxModel model = new ComboBoxModel();
      model.add("Debug");
      model.add("Release");
      return model;
    }

    public final ListBoxModel doFillSdkItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_DefaultSDK(), null);
      DotNetSDK.addSdks(model);
      return model;
    }

    public final ListBoxModel doFillVerbosityItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_Verbosity_Default(), null);
      model.add(Messages.DotNet_Verbosity_Quiet(), "q");
      model.add(Messages.DotNet_Verbosity_Minimal(), "m");
      model.add(Messages.DotNet_Verbosity_Normal(), "n");
      model.add(Messages.DotNet_Verbosity_Detailed(), "d");
      model.add(Messages.DotNet_Verbosity_Diagnostic(), "diag");
      return model;
    }

  }

  //endregion

}
