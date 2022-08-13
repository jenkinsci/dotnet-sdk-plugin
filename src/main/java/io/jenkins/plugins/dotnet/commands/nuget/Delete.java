package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/** A build step to run "{@code dotnet nuget delete}", deleting or unlisting a specific version of a package from a server. */
public final class Delete extends DeleteOrPush {

  /** Creates a new "{@code dotnet nuget delete}" build step. */
  @DataBoundConstructor
  public Delete() {
    super("delete");
  }

  /**
   * Adds command line arguments for this "{@code dotnet nuget delete}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link DeleteOrPush#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>The package name, if specified via {@link #setPackageName(String)}.</li>
   *   <li>The package version, if specified via {@link #setPackageVersion(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    super.addCommandLineArguments(args);
    args.addOption(this.packageName).addOption(this.packageVersion);
  }

  //region Properties

  private String packageName;

  /**
   * Gets the name of the package to delete.
   *
   * @return The name of the package to delete.
   */
  public String getPackageName() {
    return this.packageName;
  }

  /**
   * Sets the name of the package to delete.
   *
   * @param packageName The name of the package to delete.
   */
  @DataBoundSetter
  public void setPackageName(String packageName) {
    this.packageName = Util.fixEmptyAndTrim(packageName);
  }

  private String packageVersion;

  /**
   * The version of the package to delete.
   *
   * @return The version of the package to delete.
   */
  public String getPackageVersion() {
    return this.packageVersion;
  }

  /**
   * Sets the version of the package to delete.
   *
   * @param packageVersion The version of the package to delete.
   */
  @DataBoundSetter
  public void setPackageVersion(String packageVersion) {
    this.packageVersion = Util.fixEmptyAndTrim(packageVersion);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet nuget delete}" build steps. */
  @Extension
  @Symbol("dotnetNuGetDelete")
  public static final class DescriptorImpl extends NuGetCommandDescriptor {

    /** Creates a new "{@code dotnet nuget delete}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Performs validation on a NuGet package name.
     *
     * @param value The value to validate.
     * @param item  The item being configured.
     *
     * @return The result of the validation.
     */
    @NonNull
    @POST
    public FormValidation doCheckPackageName(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      value = Util.fixEmptyAndTrim(value);
      if (value != null && value.split(" \r\t\n", 2).length != 1) {
        return FormValidation.error(Messages.NuGet_Delete_InvalidPackageName());
      }
      return FormValidation.ok();
    }

    /**
     * Performs validation on a NuGet package version.
     *
     * @param value       The value to validate.
     * @param packageName The name of package for which a version is being validated.
     * @param item        The item being configured.
     *
     * @return The result of the validation.
     */
    @NonNull
    @POST
    public FormValidation doCheckPackageVersion(@CheckForNull @QueryParameter String value,
                                                @CheckForNull @QueryParameter String packageName,
                                                @CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      value = Util.fixEmptyAndTrim(value);
      if (value != null && value.split(" \r\t\n", 2).length != 1) {
        return FormValidation.error(Messages.NuGet_Delete_InvalidPackageVersion());
      }
      // TODO: Maybe do some basic semantic version validation?
      packageName = Util.fixEmptyAndTrim(packageName);
      if (packageName == null && value != null) {
        return FormValidation.warning(Messages.NuGet_Delete_PackageVersionWithoutName());
      }
      if (packageName != null && value == null) {
        return FormValidation.error(Messages.NuGet_Delete_PackageNameWithoutVersion());
      }
      return FormValidation.ok();
    }

    /**
     * Fills a listbox with all possible API keys (string credentials) defined in the system.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    public ListBoxModel doFillApiKeyIdItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      return DotNetUtils.getStringCredentialsList(true);
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.NuGet_Delete_DisplayName();
    }

  }

  //endregion

}
