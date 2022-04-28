package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.console.ConsoleLogFilter;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.console.DiagnosticFilter;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.io.IOException;
import java.util.Map;

/** A .NET build wrapper, for .NET pipeline steps. This makes a .NET SDK available during a specific script section. */
public class DotNetWrapper extends SimpleBuildWrapper {

  /** Creates a new .NET wrapper. */
  @DataBoundConstructor
  public DotNetWrapper() {
  }

  private String sdk;

  /**
   * Gets the name of the SDK that this wrapper will make available.
   *
   * @return The name of the SDK that this wrapper will make available, or {@code null} if none was set (which will cause a runtime
   * exception if the wrapper gets used).
   */
  @CheckForNull
  public String getSdk() {
    return this.sdk;
  }

  /**
   * Sets the name of the SDK that this wrapper will make available.
   *
   * @param sdk The name of the SDK to make available.
   */
  @DataBoundSetter
  public void setSdk(@CheckForNull String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  private boolean specificSdkVersion = false;

  /**
   * Determines whether this .NET wrapper will enforce a specific SDK version.
   * <p>
   * This mainly matters for Windows agents that have an SDK installed at the system level. Some SDK versions will always run the
   * most recent SDK version available on the system; with this set, a {@code global.json} file will be created to ensure a specific
   * SDK version gets used.
   *
   * @return {@code true} when {@code global.json} will be used to ensure a specific SDK version gets used; {@code false} otherwise.
   */
  public boolean isSpecificSdkVersion() {
    return this.specificSdkVersion;
  }

  /**
   * Determines whether this .NET wrapper should enforce a specific SDK version.
   * <p>
   * This mainly matters for Windows agents that have an SDK installed at the system level. Some SDK versions will always run the
   * most recent SDK version available on the system; with this set, a {@code global.json} file will be created to ensure a specific
   * SDK version gets used.
   *
   * @param specificSdkVersion {@code true} if {@code global.json} should be used to ensure a specific SDK version gets used;
   *                           {@code false} otherwise.
   */
  @DataBoundSetter
  public void setSpecificSdkVersion(boolean specificSdkVersion) {
    this.specificSdkVersion = specificSdkVersion;
  }

  /**
   * Sets up the .NET wrapper.
   * <p>
   * This will ensure that a valid SDK is specified, then extends the environment with the SDK's variables. If requested, it will
   * also set up a {@code global.json} (and arrange for it to be cleaned up when the wrapper ends).
   *
   * @param context            The context the wrapper is run in.
   * @param build              The build in which this wrapper is being run.
   * @param workspace          The workspace in which the wrapper is being activated.
   * @param launcher           The launcher the wrapper can use to execute programs, if needed.
   * @param listener           The listener for the build.
   * @param initialEnvironment The initial environment for the wrapper.
   */
  @Override
  public void setUp(@NonNull Context context, @NonNull Run<?, ?> build, @NonNull FilePath workspace, @NonNull Launcher launcher,
                    @NonNull TaskListener listener, @NonNull EnvVars initialEnvironment) throws IOException, InterruptedException {
    if (this.sdk == null)
      throw new AbortException(String.format(Messages.DotNetWrapper_NoSDK(), this.sdk));
    final DotNetSDK sdkInstance = Jenkins.get().getDescriptorByType(DotNetSDK.DescriptorImpl.class)
      .prepareAndValidateInstance(this.sdk, workspace, initialEnvironment, listener);
    sdkInstance.ensureExecutableExists(launcher);
    { // Update Environment
      final EnvVars modified = new EnvVars();
      sdkInstance.buildEnvVars(modified);
      for (Map.Entry<String, String> entry : modified.entrySet())
        context.env(entry.getKey(), entry.getValue());
    }
    if (this.specificSdkVersion) {
      if (sdkInstance.createGlobalJson(workspace, listener))
        context.setDisposer(new GlobalJsonRemover());
    }
  }

  /** Disposer to clean up a created {@code global.json} file. */
  private static class GlobalJsonRemover extends Disposer {

    private static final long serialVersionUID = 4748633873948298689L;

    /**
     * Tears down the wrapper context, removing a created {@code global.json} file.
     *
     * @param build     The build in which this wrapper is being run.
     * @param workspace The workspace in which the wrapper is being activated.
     * @param launcher  The launcher the wrapper can use to execute programs, if needed.
     * @param listener  The listener for the build.
     */
    @Override
    public void tearDown(@NonNull Run<?, ?> build, @NonNull FilePath workspace, @NonNull Launcher launcher,
                         @NonNull TaskListener listener) {
      DotNetSDK.removeGlobalJson(workspace, listener);
    }

  }

  /**
   * Creates a new logger decorator for the wrapper.
   *
   * @param build The build in which this wrapper is being run.
   *
   * @return A new {@link DiagnosticFilter} instance.
   */
  @CheckForNull
  @Override
  public ConsoleLogFilter createLoggerDecorator(@NonNull Run<?, ?> build) {
    return new DiagnosticFilter();
  }

  //region DescriptorImpl

  /** The descriptor for the .NET wrapper. */
  @Extension
  @Symbol("withDotNet")
  public static class DescriptorImpl extends BuildWrapperDescriptor {

    /**
     * Checks that a value is a valid .NET SDK name.
     * <p>
     * Because the value is set from a list box (so selecting invalid values is not possible), this only ensures that it's filled.
     *
     * @param value The value to check.
     * @param item  The item being configured.
     *
     * @return The validation result.
     */
    @NonNull
    @POST
    public FormValidation doCheckSdk(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      return FormValidation.validateRequired(value);
    }

    /**
     * Fills a listbox with the available .NET SDKs.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    public ListBoxModel doFillSdkItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNetWrapper_NotSelected(), "");
      DotNetSDK.addSdks(model);
      return model;
    }

    /**
     * Gets the display name for the .NET wrapper (as used in the project configuration UI).
     *
     * @return "With .NET", or a localized equivalent.
     */
    @Override
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetWrapper_DisplayName();
    }

    /**
     * Determines whether the .NET wrapper is applicable.
     *
     * @param item The project context.
     *
     * @return {@code true} if .NET SDK installations have been configured; {@code false} otherwise.
     */
    @Override
    public boolean isApplicable(@CheckForNull AbstractProject<?, ?> item) {
      return DotNetSDK.hasConfiguration();
    }

  }

  //endregion

}
