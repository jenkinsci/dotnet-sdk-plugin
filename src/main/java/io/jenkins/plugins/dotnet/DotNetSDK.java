package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.PersistentDescriptor;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.*;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.XStream2;
import io.jenkins.plugins.dotnet._dotnet.Messages;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/** Information about .NET SDKs. */
public final class DotNetSDK extends ToolInstallation implements NodeSpecific<DotNetSDK>, EnvironmentSpecific<DotNetSDK> {

  private final boolean telemetryOptOut;

  public DotNetSDK(String name, String home, boolean telemetryOptOut) {
    super(name, home, Collections.emptyList());
    this.telemetryOptOut = telemetryOptOut;
  }

  @DataBoundConstructor
  public DotNetSDK(String name, String home, List<? extends ToolProperty<?>> properties, boolean telemetryOptOut) {
    super(name, home, properties);
    this.telemetryOptOut = telemetryOptOut;
  }

  public static void addSdks(@NonNull ListBoxModel model) {
    final DotNetSDK[] sdks = Jenkins.get().getDescriptorByType(DescriptorImpl.class).getInstallations();
    for (final DotNetSDK _sdk : sdks)
      model.add(_sdk.getName());
  }

  @Override
  public void buildEnvVars(EnvVars env) {
    env.put("DOTNET_HOME", this.getHome());
    env.put("PATH+DOTNET", this.getHome());
    if (this.telemetryOptOut)
      env.put("DOTNET_CLI_TELEMETRY_OPTOUT", "1");
  }

  @Override
  public DotNetSDK forEnvironment(EnvVars envVars) {
    return new DotNetSDK(this.getName(), envVars.expand(getHome()), this.telemetryOptOut);
  }

  @Override
  public DotNetSDK forNode(@NonNull Node node, TaskListener log) throws IOException, InterruptedException {
    return new DotNetSDK(this.getName(), translateFor(node, log), this.telemetryOptOut);
  }

  /**
   * Determines whether or not any .NET SDKs have been configured.
   *
   * @return {@code true} when at least one .NET SDK has been configured; otherwise, {@code false}.
   */
  public static boolean sdkConfigured() {
    final DotNetSDK[] sdks = Jenkins.get().getDescriptorByType(DescriptorImpl.class).getInstallations();
    return sdks != null && sdks.length > 0;
  }

  //region ConverterImpl

  public static class ConverterImpl extends ToolConverter {

    public ConverterImpl(XStream2 xstream) { super(xstream); }

    @Override protected String oldHomeField(ToolInstallation obj) {
      return null;
    }

  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetsdk")
  public static class DescriptorImpl extends ToolDescriptor<DotNetSDK> implements PersistentDescriptor {

    @Override
    protected FormValidation checkHomeDirectory(File home) {
      { // Top level needs a 'dotnet' executable
        final File dotnet_exe = new File(home, "dotnet.exe");
        final File dotnet = new File(home, "dotnet");
        if (!(dotnet_exe.exists() && dotnet_exe.isFile()) && !(dotnet.exists() && dotnet.isFile()))
          return FormValidation.error("No 'dotnet' executable present.");
        if (!dotnet_exe.canExecute() && !dotnet.canExecute())
          return FormValidation.error("A 'dotnet' executable is present but cannot be executed.");
      }
      { // Should have 'sdk' and 'shared/Microsoft.NETCore.App' subdirs
        final File sdk = new File(home, "sdk");
        if (!sdk.exists() || !sdk.isDirectory())
          return FormValidation.error("No 'sdk' subdirectory present.");
        final File sharedNetCoreApp = new File(home, "shared/Microsoft.NETCore.App");
        if (!sharedNetCoreApp.exists() || !sharedNetCoreApp.isDirectory())
          return FormValidation.error("No 'shared/Microsoft.NETCore.App' subdirectory present.");
        // Potential further checks:
        // - at least one folder under 'sdk' should contain 'dotnet.dll'
        // - a folder of the same name must exist under shared/Microsoft.NETCore.App
      }
      return FormValidation.ok();
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetSDK_DisplayName();
    }

    @Override
    public void setInstallations(DotNetSDK... sdks) {
      super.setInstallations(sdks);
      this.save();
    }

  }

  //endregion

}
