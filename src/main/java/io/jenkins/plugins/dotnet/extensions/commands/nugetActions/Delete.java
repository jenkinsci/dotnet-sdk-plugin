package io.jenkins.plugins.dotnet.extensions.commands.nugetActions;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.Item;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

public class Delete extends DeleteOrPush {

  private static final long serialVersionUID = -2865784229222476789L;

  @DataBoundConstructor
  public Delete() {
  }

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption("delete");
    super.addCommandLineArguments(arguments);
    arguments.addOption(this.packageName);
    arguments.addOption(this.packageVersion);
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

  public interface StaplerMethods extends DeleteOrPush.StaplerMethods {

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
    default FormValidation doCheckPackageName(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
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
    default FormValidation doCheckPackageVersion(@CheckForNull @QueryParameter String value,
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

  }

  @Extension
  @Symbol("delete")
  public static final class DescriptorImpl extends NuGetActionDescriptor implements StaplerMethods {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.NuGet_Delete_DisplayName();
    }

  }

}
