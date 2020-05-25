package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.console.ConsoleLogFilter;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.console.DiagnosticFilter;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

/** A .NET wrapper, for .NET pipeline steps. */
public class DotNetWrapper extends SimpleBuildWrapper {

  @DataBoundConstructor
  public DotNetWrapper() {
  }

  private String sdk;

  public String getSdk() {
    return this.sdk;
  }

  @DataBoundSetter
  public void setSdk(String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  private boolean specificSdkVersion;

  public boolean isSpecificSdkVersion() {
    return this.specificSdkVersion;
  }

  @DataBoundSetter
  public void setSpecificSdkVersion(boolean specificSdkVersion) {
    this.specificSdkVersion = specificSdkVersion;
  }

  @Override
  public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
    if (this.sdk == null)
      throw new AbortException(String.format(Messages.DotNetWrapper_NoSDK(), this.sdk));
    final DotNetSDK sdkInstance = Jenkins.get().getDescriptorByType(DotNetSDK.DescriptorImpl.class).prepareAndValidateInstance(this.sdk, workspace, initialEnvironment, listener);
    sdkInstance.ensureExecutableExists(launcher);
    { // Update Environment
      final EnvVars modified = new EnvVars();
      sdkInstance.buildEnvVars(modified);
      for (Map.Entry<String, String> entry : modified.entrySet())
        context.env(entry.getKey(), entry.getValue());
    }
    if (this.specificSdkVersion) {
      if (sdkInstance.createGlobalJson(workspace, launcher, listener))
        context.setDisposer(new GlobalJsonRemover(sdkInstance));
    }
  }

  private static class GlobalJsonRemover extends Disposer {

    private final DotNetSDK sdk;

    public GlobalJsonRemover(@NonNull DotNetSDK sdk) {
      this.sdk = sdk;
    }

    @Override
    public void tearDown(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
      this.sdk.removeGlobalJson(workspace);
    }

  }

  @CheckForNull
  @Override
  public ConsoleLogFilter createLoggerDecorator(@Nonnull Run<?, ?> build) {
    return new DiagnosticFilter();
  }

  //region DescriptorImpl

  @Extension
  @Symbol("withDotNet")
  public static class DescriptorImpl extends BuildWrapperDescriptor {

    @Override
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetWrapper_DisplayName();
    }

    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return DotNetSDK.hasConfiguration();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckSdk(@QueryParameter String value) {
      return FormValidation.validateRequired(value);
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillSdkItems() {
      final ListBoxModel model = new ListBoxModel();
      DotNetSDK.addSdks(model);
      return model;
    }

  }

  //endregion

}
