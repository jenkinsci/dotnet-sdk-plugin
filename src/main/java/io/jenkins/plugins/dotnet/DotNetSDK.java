package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import jenkins.security.MasterToSlaveCallable;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.CheckForNull;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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

  public String ensureExecutableExists(@NonNull Launcher launcher) throws IOException, InterruptedException {
    final String executable = DotNetSDK.getExecutableFileName(launcher);
    final String fullExecutablePath = this.getSdkFilePath(launcher, executable);
    if (fullExecutablePath == null)
      throw new AbortException(Messages.DotNetSDK_ExecutableNotFound(this.getName()));
    return fullExecutablePath;
  }

  @Override
  public DotNetSDK forEnvironment(EnvVars envVars) {
    return new DotNetSDK(this.getName(), envVars.expand(this.getHome()), this.telemetryOptOut);
  }

  @Override
  public DotNetSDK forNode(@NonNull Node node, TaskListener listener) throws IOException, InterruptedException {
    return new DotNetSDK(this.getName(), this.translateFor(node, listener), this.telemetryOptOut);
  }

  public static String getExecutableFileName(@NonNull Launcher launcher) {
    return launcher.isUnix() ? "dotnet" : "dotnet.exe";
  }

  public String getSdkFilePath(Launcher launcher, String name) throws IOException, InterruptedException {
    final VirtualChannel channel = launcher.getChannel();
    if (channel != null)
      return channel.call(new GetSdkFilePath(this.getHome(), name));
    return null;
  }

  private static final class GetSdkFilePath extends MasterToSlaveCallable<String, IOException> implements Serializable {

    private final String home;
    private final String name;

    public GetSdkFilePath(String home, String name) {
      this.home = home;
      this.name = name;
    }

    @Override
    public String call() {
      final String expandedHome = Util.replaceMacro(this.home, EnvVars.masterEnvVars);
      final File file = new File(expandedHome, this.name);
      if (file.exists())
        return file.getPath();
      return null;
    }

  }

  /**
   * Determines whether or not any .NET SDKs have been configured.
   *
   * @return {@code true} when at least one .NET SDK has been configured; otherwise, {@code false}.
   */
  public static boolean hasConfiguration() {
    final DotNetSDK[] sdks = Jenkins.get().getDescriptorByType(DescriptorImpl.class).getInstallations();
    return sdks != null && sdks.length > 0;
  }

  //region ConverterImpl

  @SuppressWarnings("unused")
  public static class ConverterImpl extends ToolConverter {

    public ConverterImpl(XStream2 xstream) {
      super(xstream);
    }

    @Override
    protected String oldHomeField(ToolInstallation obj) {
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
      // This can be used to check the existence of a file on the server, so needs to be protected.
      if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER))
        return FormValidation.ok();
      { // Top level needs a 'dotnet' executable
        final File dotnet_exe = new File(home, "dotnet.exe");
        final File dotnet = new File(home, "dotnet");
        if (!(dotnet_exe.exists() && dotnet_exe.isFile()) && !(dotnet.exists() && dotnet.isFile()))
          return FormValidation.error(Messages.DotNetSDK_Home_NoExecutable());
        if (!dotnet_exe.canExecute() && !dotnet.canExecute())
          return FormValidation.error(Messages.DotNetSDK_Home_NotExecutable());
      }
      { // Should have 'sdk' and 'shared/Microsoft.NETCore.App' subdirs
        final File sdk = new File(home, "sdk");
        if (!sdk.exists() || !sdk.isDirectory())
          return FormValidation.error(Messages.DotNetSDK_Home_NoSdkSubdir());
        final File sharedNetCoreApp = new File(home, "shared/Microsoft.NETCore.App");
        if (!sharedNetCoreApp.exists() || !sharedNetCoreApp.isDirectory())
          return FormValidation.error(Messages.DotNetSDK_Home_NoSharedNetCoreSubdir());
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

    public DotNetSDK prepareAndValidateInstance(@NonNull String name, @NonNull FilePath workspace, @CheckForNull EnvVars env, @CheckForNull TaskListener listener) throws IOException, InterruptedException {
      DotNetSDK sdkInstance = null;
      {
        for (final DotNetSDK sdk : this.getInstallations()) {
          if (sdk.getName().equals(name)) {
            sdkInstance = sdk;
            break;
          }
        }
        if (sdkInstance == null)
          throw new AbortException(Messages.DotNetSDK_UnknownSDK(name));
      }
      { // Apply NodeSpecific
        final Node node;
        {
          final Computer computer = workspace.toComputer();
          node = (computer != null) ? computer.getNode() : null;
        }
        if (node == null)
          throw new AbortException(Messages.DotNetSDK_NoNode());
        sdkInstance = sdkInstance.forNode(node, listener);
      }
      if (env != null) // Apply EnvironmentSpecific
        sdkInstance = sdkInstance.forEnvironment(env);
      return sdkInstance;
    }

    @Override
    public void setInstallations(DotNetSDK... sdks) {
      super.setInstallations(sdks);
      this.save();
    }

  }

  //endregion

}
