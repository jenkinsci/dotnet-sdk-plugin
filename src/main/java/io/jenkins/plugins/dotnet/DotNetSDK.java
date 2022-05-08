package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.PersistentDescriptor;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/** An installation of a .NET SDK as a global tool. */
public final class DotNetSDK extends ToolInstallation implements NodeSpecific<DotNetSDK>, EnvironmentSpecific<DotNetSDK> {

  private static final long serialVersionUID = 1834789641052956539L;

  /**
   * An environment variable that will be set to the full path to the SDK (backward compatibility).
   *
   * @deprecated Use {@link #ROOT_ENVIRONMENT_VARIABLE} instead; that environment variable is also used by the SDK itself.
   */
  @Deprecated
  @SuppressWarnings("DeprecatedIsStillUsed")
  public static final String HOME_ENVIRONMENT_VARIABLE = "DOTNET_SDK_JENKINS_TOOL_HOME";

  /** The environment variable that will be set to the full path to the SDK (used by the SDK in some cases). */
  // FIXME: There is also a %DOTNET_ROOT(x86)%, used when running x86 programs on an x64 host. But we can't tell whether that is the
  // FIXME: case, so we can't know whether it would be appropriate to set it.
  public static final String ROOT_ENVIRONMENT_VARIABLE = "DOTNET_ROOT";

  /**
   * Creates a new .NET SDK installation.
   *
   * @param name The name for the installation.
   * @param home The path to the SDK.
   */
  public DotNetSDK(@NonNull String name, @NonNull String home) {
    super(name, home, Collections.emptyList());
    this.configuration = ExtensionList.lookupSingleton(DotNetConfiguration.class);
  }

  /**
   * Creates a new .NET SDK installation.
   *
   * @param name       The name for the installation.
   * @param home       The path to the SDK.
   * @param properties Additional properties for the SDK installation.
   */
  @DataBoundConstructor
  public DotNetSDK(@NonNull String name, @NonNull String home, @CheckForNull List<? extends ToolProperty<?>> properties) {
    super(name, home, properties);
    this.configuration = ExtensionList.lookupSingleton(DotNetConfiguration.class);
  }

  @NonNull
  private final DotNetConfiguration configuration;

  private boolean telemetryOptOut = false;

  /**
   * Determines whether the telemetry opt-out is set.
   *
   * @return {@code true} when the telemetry opt-out is set; {@code false} otherwise.
   */
  public boolean isTelemetryOptOut() {
    return this.telemetryOptOut;
  }

  /**
   * Determines whether the telemetry opt-out should be set.
   *
   * @param telemetryOptOut {@code true} to opt out of telemetry; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setTelemetryOptOut(boolean telemetryOptOut) {
    this.telemetryOptOut = telemetryOptOut;
  }

  /**
   * Adds the names of all defined .NET SDK installations to a listbox model.
   *
   * @param model The listbox model to add the .NET SDK installations to.
   */
  public static void addSdks(@NonNull ListBoxModel model) {
    final DescriptorImpl descriptor = Jenkins.get().getDescriptorByType(DescriptorImpl.class);
    if (descriptor == null) {
      return;
    }
    final DotNetSDK[] sdks = descriptor.getInstallations();
    for (final DotNetSDK _sdk : sdks) {
      model.add(_sdk.getName());
    }
  }

  /**
   * Sets up environment variables for this .NET SDK installation.
   *
   * @param env The environment variables to add values to.
   */
  @Override
  public void buildEnvVars(@NonNull EnvVars env) {
    { // Store the home location in the environment, and add it to PATH
      final String home = this.getHome();
      env.put(DotNetSDK.HOME_ENVIRONMENT_VARIABLE, home);
      env.put(DotNetSDK.ROOT_ENVIRONMENT_VARIABLE, home);
      env.put("PATH+DOTNET", home);
    }
    if (this.configuration.isTelemetryOptOut() || this.telemetryOptOut) {
      env.put("DOTNET_CLI_TELEMETRY_OPTOUT", "1");
    }
  }

  /**
   * Create a {@code global.json} in the specified location, forcing the use of this .NET SDK's exact version.
   *
   * @param dir      The location where the {@code global.json} should be created.
   * @param listener The task listener to use for output
   *
   * @return {@code true} when a {@code global.json} file was created; {@code false} otherwise.
   */
  public boolean createGlobalJson(@NonNull FilePath dir, @NonNull TaskListener listener) {
    final String version;
    try {
      final FilePath home = this.getHomePath(dir.getChannel());
      if (home == null) {
        final String problem = Messages.DotNetSDK_GlobalJson_NoHome();
        listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), problem));
        return false;
      }
      final FilePath sdkRoot = home.child("sdk");
      if (sdkRoot.exists()) {
        String singleSdkVersion = null;
        for (final FilePath sdkDir : sdkRoot.listDirectories()) {
          // Assumption: the presence of 'dotnet.dll' is a correct way of distinguishing between SDK dirs and things like the NuGet
          //             fallback folder.
          if (!sdkDir.child("dotnet.dll").exists()) {
            continue;
          }
          if (singleSdkVersion != null) {
            final String problem = Messages.DotNetSDK_GlobalJson_MultiSdk(singleSdkVersion, sdkDir.getName());
            listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), problem));
          }
          singleSdkVersion = sdkDir.getName();
        }
        version = singleSdkVersion;
      }
      else {
        final String problem = Messages.DotNetSDK_GlobalJson_NoSdk();
        listener.getLogger().println(Messages.DotNetSDK_GlobalJson_NoVersion(this.getName(), problem));
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
    }
    catch (Throwable t) {
      listener.getLogger().println(Messages.DotNetSDK_GlobalJson_CreationFailed(this.getName(), version, t));
      return false;
    }
    listener.getLogger().println(Messages.DotNetSDK_GlobalJson_CreationDone(this.getName(), version));
    return true;
  }

  /**
   * Ensures that the {@code dotnet} executable exists in this .NET SDK installation.
   *
   * @param launcher The launcher to use for the verification.
   *
   * @return The full path to the {@code dotnet} executable in this .NET SDK installation.
   *
   * @throws AbortException       When the executable could not be found.
   * @throws IOException          When an I/O error occurs.
   * @throws InterruptedException When processing is interrupted.
   */
  @NonNull
  public String ensureExecutableExists(@NonNull Launcher launcher) throws IOException, InterruptedException {
    final FilePath homePath = this.getHomePath(launcher.getChannel());
    if (homePath == null || !homePath.exists()) {
      throw new AbortException(Messages.DotNetSDK_NoHome(this.getName()));
    }
    final FilePath fullExecutablePath;
    {
      final String executable = DotNetSDK.getExecutableFileName(launcher);
      fullExecutablePath = homePath.child(executable);
      if (!fullExecutablePath.exists()) {
        throw new AbortException(Messages.DotNetSDK_NoExecutable(this.getName(), executable));
      }
    }
    return fullExecutablePath.getRemote();
  }

  /**
   * Creates a copy of this .NET SDK installation that has the specified environment variables applied.
   *
   * @param envVars The environment variables to apply.
   *
   * @return A copy of this .NET SDK installation that has the specified environment variables applied.
   */
  @Override
  @NonNull
  public DotNetSDK forEnvironment(@NonNull EnvVars envVars) {
    final DotNetSDK sdk = new DotNetSDK(this.getName(), envVars.expand(this.getHome()));
    sdk.setTelemetryOptOut(this.telemetryOptOut);
    return sdk;
  }

  /**
   * Creates a copy of this .NET SDK installation for use on the specified node.
   *
   * @param node The node to use the .NET SDK installation on.
   *
   * @return A copy of this .NET SDK installation for use on {@code node}.
   */
  @Override
  @NonNull
  public DotNetSDK forNode(@NonNull Node node, @NonNull TaskListener listener) throws IOException, InterruptedException {
    final DotNetSDK sdk = new DotNetSDK(this.getName(), this.translateFor(node, listener));
    sdk.setTelemetryOptOut(this.telemetryOptOut);
    return sdk;
  }

  /**
   * Returns the file name for the {@code dotnet} executable on the agent platform.
   *
   * @param launcher The launcher to use to determine the agent platform.
   *
   * @return {@code dotnet.exe} when {@code launcher} represents a Windows agent, {@code dotnet} otherwise.
   */
  @NonNull
  public static String getExecutableFileName(@NonNull Launcher launcher) {
    return launcher.isUnix() ? "dotnet" : "dotnet.exe";
  }

  /**
   * Determines the file path for this SDK's home directory.
   *
   * @param channel The remote context to use for the file path.
   *
   * @return A file path representing this SDK's home directory, or {@code null} if no home directory was set.
   */
  @CheckForNull
  public FilePath getHomePath(@CheckForNull VirtualChannel channel) {
    final String home = this.getHome();
    if (home == null) {
      return null;
    }
    return new FilePath(channel, home);
  }

  /**
   * Determines whether any .NET SDKs have been configured.
   *
   * @return {@code true} when at least one .NET SDK has been configured; otherwise, {@code false}.
   */
  public static boolean hasConfiguration() {
    final DescriptorImpl descriptor = Jenkins.get().getDescriptorByType(DescriptorImpl.class);
    if (descriptor == null) {
      return false;
    }
    final DotNetSDK[] sdks = descriptor.getInstallations();
    return sdks != null && sdks.length > 0;
  }

  /**
   * Removes a {@code global.json} file (as previously created via {@link #createGlobalJson(FilePath, TaskListener)}.
   *
   * @param dir      The location containing the {@code global.json} file to remove.
   * @param listener The task listener to use for output.
   */
  public static void removeGlobalJson(@NonNull FilePath dir, @NonNull TaskListener listener) {
    final FilePath globalJson = dir.child("global.json");
    try {
      globalJson.delete();
      listener.getLogger().println(Messages.DotNetSDK_GlobalJson_DeletionDone(globalJson.getRemote()));
    }
    catch (Throwable t) {
      listener.getLogger().println(Messages.DotNetSDK_GlobalJson_DeletionFailed(globalJson.getRemote(), t));
    }
  }

  //region DescriptorImpl

  /** A descriptor for .NET SDK installations. */
  @Extension
  @Symbol("dotnetsdk")
  public static class DescriptorImpl extends ToolDescriptor<DotNetSDK> implements PersistentDescriptor {

    /**
     * Performs validation on the installation directory for a .NET SDK.
     *
     * @param home The directory to validate.
     *
     * @return The validation result.
     */
    @Override
    @NonNull
    protected FormValidation checkHomeDirectory(@NonNull File home) {
      // This can be used to check the existence of a file on the server, so needs to be protected.
      Jenkins.get().checkPermission(Jenkins.ADMINISTER);
      { // Top level needs a 'dotnet' executable
        final File dotnet_exe = new File(home, "dotnet.exe");
        final File dotnet = new File(home, "dotnet");
        if (!(dotnet_exe.exists() && dotnet_exe.isFile()) && !(dotnet.exists() && dotnet.isFile())) {
          return FormValidation.error(Messages.DotNetSDK_Home_NoExecutable());
        }
        if (!dotnet_exe.canExecute() && !dotnet.canExecute()) {
          return FormValidation.error(Messages.DotNetSDK_Home_NotExecutable());
        }
      }
      { // Should have 'sdk' and 'shared/Microsoft.NETCore.App' subdirs
        final File sdk = new File(home, "sdk");
        if (!sdk.exists() || !sdk.isDirectory()) {
          return FormValidation.error(Messages.DotNetSDK_Home_NoSdkSubdir());
        }
        final File sharedNetCoreApp = new File(home, "shared/Microsoft.NETCore.App");
        if (!sharedNetCoreApp.exists() || !sharedNetCoreApp.isDirectory()) {
          return FormValidation.error(Messages.DotNetSDK_Home_NoSharedNetCoreSubdir());
        }
        // Potential further checks:
        // - at least one folder under 'sdk' should contain 'dotnet.dll'
        // - a folder of the same name must exist under shared/Microsoft.NETCore.App
      }
      return FormValidation.ok();
    }

    /**
     * Gets the default installer to use for a .NET SDK installation.
     *
     * @return A list containing a single {@link DotNetSDKInstaller} instance.
     */
    @Override
    @NonNull
    public List<? extends ToolInstaller> getDefaultInstallers() {
      return Collections.singletonList(new DotNetSDKInstaller(""));
    }

    /**
     * Returns the display name for .NET SDK installations.
     *
     * @return ".NET SDK" or a localized equivalent.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetSDK_DisplayName();
    }

    /**
     * Gets a .NET SDK installation by its name, and prepare it for use in the specified context.
     *
     * @param name      The name of the configured .NET SDK installation.
     * @param workspace The workspace to use.
     * @param env       The environment to use.
     * @param listener  The task listener to use.
     *
     * @return The requested .NET SDK installation.
     *
     * @throws AbortException       When the SDK installation could not be set up.
     * @throws IOException          Then an I/O error occurs.
     * @throws InterruptedException When processing is interrupted.
     */
    @NonNull
    public DotNetSDK prepareAndValidateInstance(@NonNull String name, @NonNull FilePath workspace, @NonNull EnvVars env,
                                                @NonNull TaskListener listener) throws IOException, InterruptedException {
      DotNetSDK sdkInstance = null;
      {
        for (final DotNetSDK sdk : this.getInstallations()) {
          if (sdk.getName().equals(name)) {
            sdkInstance = sdk;
            break;
          }
        }
        if (sdkInstance == null) {
          throw new AbortException(Messages.DotNetSDK_UnknownSDK(name));
        }
      }
      { // Apply NodeSpecific
        final Node node;
        {
          final Computer computer = workspace.toComputer();
          node = (computer != null) ? computer.getNode() : null;
        }
        if (node == null) {
          throw new AbortException(Messages.DotNetSDK_NoNode());
        }
        sdkInstance = sdkInstance.forNode(node, listener);
      }
      sdkInstance = sdkInstance.forEnvironment(env);
      return sdkInstance;
    }

    /**
     * Sets the set of configured .NET SDK installations.
     *
     * @param sdks The set of configured .NET SDK installations.
     */
    @Override
    public void setInstallations(@NonNull DotNetSDK... sdks) {
      super.setInstallations(sdks);
      this.save();
    }

  }

  //endregion

}
