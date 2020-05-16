package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.Map;

/** A .NET wrapper, for .NET pipeline steps. */
public class DotNetWrapper extends SimpleBuildWrapper {

  private String sdk;

  // TODO: Add a "force exact version" flag; if set, the setup will create/update global.json in the workspace root to specify
  // TODO: the exact SDK version (to be provided by DotNetSDK). Otherwise, on Windows running 'dotnet' will use the highest version
  // TODO: available, looking at both the explict SDK installed by Jenkins and the system-wide installed SDKs.
  // TODO: https://docs.microsoft.com/en-us/dotnet/core/versions/selection#the-sdk-uses-the-latest-installed-version

  // TODO: Maybe add a global configuration setting to opt out of the telemetry.
  // TODO: See https://docs.microsoft.com/en-us/dotnet/core/tools/telemetry

  @DataBoundConstructor
  public DotNetWrapper() {}

  public String getSdk() {
    return this.sdk;
  }

  @DataBoundSetter
  public void setSdk(String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  @Override
  public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
    // FIXME: Or should the constructor have already stopped null from being assigned?
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
  }

  // TODO: Custom Logger Decorator?

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

    public ListBoxModel doFillSdkItems() {
      final ListBoxModel model = new ListBoxModel();
      DotNetSDK.addSdks(model);
      return model;
    }

  }

  //endregion

}
