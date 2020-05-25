package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.*;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.CheckForNull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Information about .NET SDKs. */
public final class DotNetSDK extends ToolInstallation implements NodeSpecific<DotNetSDK>, EnvironmentSpecific<DotNetSDK> {

  private static final long serialVersionUID = 1834789641052956539L;

  public DotNetSDK(String name, String home) {
    super(name, home, Collections.emptyList());
  }

  @DataBoundConstructor
  public DotNetSDK(String name, String home, List<? extends ToolProperty<?>> properties) {
    super(name, home, properties);
  }

  // FIXME: The 'telemetry opt-out' settings makes more sense as a global configuration option.
  // FIXME: However, attempts at creating a GlobalConfiguration failed, with the checkbox not getting shown in/under the section
  // FIXME: (despite using the same setup done by the GlobalMavenConfig.

  private boolean telemetryOptOut = true;

  public boolean isTelemetryOptOut() {
    return this.telemetryOptOut;
  }

  @DataBoundSetter
  public void setTelemetryOptOut(boolean telemetryOptOut) {
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

  public boolean createGlobalJson(@NonNull FilePath dir, @NonNull Launcher launcher, @NonNull TaskListener listener) {
    final String version;
    try {
      final FilePath home = this.getHomePath(launcher);
      if (home == null) {
        listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), Messages.DotNetSDK_GlobalJson_NoHome()));
        return false;
      }
      final FilePath sdkRoot = home.child("sdk");
      if (sdkRoot.exists()) {
        String singleSdkVersion = null;
        for (final FilePath sdkDir : sdkRoot.listDirectories()) {
          // Assumption: the presence of 'dotnet.dll' is a correct way of distinguishing between SDK dirs and things like the NuGet
          //             fallback folder. So far this has been shown to be true (.NET Core 1.0 up to .NET 5.0 preview 4).
          if (!sdkDir.child("dotnet.dll").exists())
            continue;
          if (singleSdkVersion != null)
            listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), Messages.DotNetSDK_GlobalJson_MultiSdk(singleSdkVersion, sdkDir.getName())));
          singleSdkVersion = sdkDir.getName();
        }
        version = singleSdkVersion;
      }
      else {
        listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), Messages.DotNetSDK_GlobalJson_NoSdk()));
        return false;
      }
    }
    catch (Throwable t) {
      listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), t));
      return false;
    }
    try {
      final String json = "{ \"sdk\": { \"version\": \"" + version + "\", \"rollback\": \"disable\" } }";
      dir.child("global.json").write(json, "UTF-8");
      listener.getLogger().println(Messages.DotNetSDK_GlobalJson_CreationDone(this.getName(), version));
      return true;
    }
    catch (Throwable t) {
      listener.getLogger().println(Messages.DotNetSDK_GlobalJson_CreationFailed(this.getName(), version, t));
      return false;
    }
  }

  public String ensureExecutableExists(@NonNull Launcher launcher) throws IOException, InterruptedException {
    final FilePath homePath = this.getHomePath(launcher);
    if (homePath == null || !homePath.exists())
      throw new AbortException(Messages.DotNetSDK_NoHome(this.getName()));
    final FilePath fullExecutablePath;
    {
      final String executable = DotNetSDK.getExecutableFileName(launcher);
      fullExecutablePath = homePath.child(executable);
      if (!fullExecutablePath.exists())
        throw new AbortException(Messages.DotNetSDK_NoExecutable(this.getName(), executable));
    }
    return fullExecutablePath.getRemote();
  }

  @Override
  public DotNetSDK forEnvironment(EnvVars envVars) {
    final DotNetSDK sdk = new DotNetSDK(this.getName(), envVars.expand(this.getHome()));
    sdk.setTelemetryOptOut(this.telemetryOptOut);
    return sdk;
  }

  @Override
  public DotNetSDK forNode(@NonNull Node node, TaskListener listener) throws IOException, InterruptedException {
    final DotNetSDK sdk = new DotNetSDK(this.getName(), this.translateFor(node, listener));
    sdk.setTelemetryOptOut(this.telemetryOptOut);
    return sdk;
  }

  public static String getExecutableFileName(@NonNull Launcher launcher) {
    return launcher.isUnix() ? "dotnet" : "dotnet.exe";
  }

  /**
   * Determines the file path for this SDK's home directory.
   *
   * @param launcher The launcher to get the remote context from.
   *
   * @return A file path representing this SDK's home directory, or {@code null} if no home directory was set.
   */
  public FilePath getHomePath(@NonNull Launcher launcher) {
    final String home = this.getHome();
    if (home == null)
      return null;
    return new FilePath(launcher.getChannel(), home);
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

  public static void removeGlobalJson(@NonNull FilePath dir) {
    final FilePath globalJson = dir.child("global.json");
    try {
      globalJson.delete();
    }
    catch (Throwable t) {
      DotNetSDK.LOGGER.log(Level.FINE, String.format("Failed to delete %s", globalJson.getRemote()), t);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DotNetSDK.class.getName());

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
