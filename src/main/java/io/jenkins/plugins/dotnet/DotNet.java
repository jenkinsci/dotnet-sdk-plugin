package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.nio.charset.Charset;

/** A builder using a .NET SDK. */
public abstract class DotNet extends Builder {

  protected DotNet(String sdk, @NonNull String mainCommand) {
    this.sdk = Util.fixEmpty(sdk);
    this.mainCommand = mainCommand;
  }

  protected void addCommandLineArguments(@NonNull ArgumentListBuilder args) {
    // to be overridden by subclass
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  @NonNull
  protected FilePath getWorkDirectory(@NonNull FilePath workspace) {
    return workspace;
  }

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
    final ArgumentListBuilder args = new ArgumentListBuilder();
    if (sdkInstance != null)
      args.add(sdkInstance.ensureExecutableExists(launcher));
    else // rely on the system path
      args.add(DotNetSDK.getExecutableFileName(launcher));
    args.add(this.mainCommand);
    if (sdkInstance != null)
      sdkInstance.buildEnvVars(env);
    // TODO: Optionally handle build variables (sensitive and otherwise)
    this.addCommandLineArguments(args);
    final FilePath workDir = this.getWorkDirectory(workspace);
    final long startTime = System.currentTimeMillis();
    try {
      final DotNetConsoleProcessor dncp = new DotNetConsoleProcessor(listener.getLogger(), Charset.defaultCharset());
      final int rc = launcher.launch().cmds(args).envs(env).stdout(dncp).pwd(workDir).join();
      listener.getLogger().printf("Exit Code: %d%n", rc);
      if (dncp.getErrors() > 0)
        return false;
      if (rc != 0 || dncp.getWarnings() > 0) // TODO: Make this configurable
        build.setResult(Result.UNSTABLE);
      return true;
    }
    catch (IOException e) {
      Util.displayIOException(e, listener);
      Functions.printStackTrace(e, listener.fatalError(Messages.DotNet_ExecutionFailed()));
      String msg = Messages.DotNet_ExecutionFailed();
      final long elapsed = System.currentTimeMillis() - startTime;
      // This is what the Ant plugin does - assume that if it fails fast, it's because the executable wasn't in the path.
      if (sdkInstance == null && elapsed >= 0 && elapsed < 1000) {
        if (DotNetSDK.hasConfiguration()) // at least one SDK configured
          msg += Messages.DotNet_ToolConfigurationNeeded();
        else
          msg += Messages.DotNet_ToolSelectionNeeded();
      }
      throw new AbortException(msg);
    }
  }

  //region Properties

  // TODO: Add a "force exact version" flag; if set, the setup will create/update global.json in the workspace root to specify
  // TODO: the exact SDK version (to be provided by DotNetSDK). Otherwise, on Windows running 'dotnet' from .NET Core 2 and up will
  // TODO: use the highest version available, looking at both the SDK installed by Jenkins and the system-wide installed SDKs.
  // TODO: https://docs.microsoft.com/en-us/dotnet/core/versions/selection#the-sdk-uses-the-latest-installed-version

  protected final String sdk;

  public String getSdk() {
    return this.sdk;
  }

  @NonNull
  private final String mainCommand;

  //endregion

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
