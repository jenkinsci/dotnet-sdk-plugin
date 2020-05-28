package io.jenkins.plugins.dotnet;

import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.VariableResolver;
import io.jenkins.plugins.dotnet.console.DiagnosticScanner;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** A builder using a .NET SDK. */
public abstract class DotNet extends Builder {

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  protected abstract void addCommandLineArguments(@Nonnull ArgumentListBuilder args, @Nonnull VariableResolver<String> resolver, @Nonnull Set<String> sensitive);

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

  public Result run(@Nonnull FilePath wd, @Nonnull EnvVars env, @Nonnull Launcher launcher, @Nonnull TaskListener listener, @Nonnull Charset cs) throws InterruptedException, IOException {
    final DotNetSDK sdkInstance;
    if (this.sdk == null)
      sdkInstance = null;
    else
      sdkInstance = this.getDescriptor().getToolDescriptor().prepareAndValidateInstance(this.sdk, wd, env, listener);
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
        sdkInstance.createGlobalJson(wd, launcher, listener);
      // Note: this MUST NOT BE CLOSED, because that also closes the build listener, causing all further output to go bye-bye
      final DiagnosticScanner scanner = new DiagnosticScanner(listener.getLogger(), cs);
      if (this.showSdkInfo) {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable, "--info");
        launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
      }
      int rc = -1;
      {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable);
        { // Add the rest of the command line. Will eventually support variable expansion.
          final VariableResolver<String> vr = DotNetUtils.RESOLVE_NOTHING;
          final Set<String> sensitive = Collections.emptySet();
          this.addCommandLineArguments(cmdLine, vr, sensitive);
        }
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
      Functions.printStackTrace(t, listener.fatalError(Messages.DotNet_ExecutionFailed()));
      throw new AbortException(Messages.DotNet_ExecutionFailed());
    }
    finally {
      if (sdkInstance != null && this.specificSdkVersion)
        DotNetSDK.removeGlobalJson(wd, listener);
    }
  }

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

  private boolean specificSdkVersion = false;

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
