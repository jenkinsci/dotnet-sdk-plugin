package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.CommandDescriptor;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

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

  public String getPackageName() {
    return this.packageName;
  }

  @DataBoundSetter
  public void setPackageName(String packageName) {
    this.packageName = Util.fixEmptyAndTrim(packageName);
  }

  private String packageVersion;

  public String getPackageVersion() {
    return this.packageVersion;
  }

  @DataBoundSetter
  public void setPackageVersion(String packageVersion) {
    this.packageVersion = Util.fixEmptyAndTrim(packageVersion);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet nuget delete}" build steps. */
  @Extension
  @Symbol("dotnetNuGetDelete")
  public static final class DescriptorImpl extends CommandDescriptor {

    /** Creates a new "{@code dotnet nuget delete}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    public FormValidation doCheckPackageName(@QueryParameter String value) {
      value = Util.fixEmptyAndTrim(value);
      // TODO: Maybe do some basic semantic version validation?
      if (value != null && value.split(" \r\t\n", 2).length != 1)
        return FormValidation.error(Messages.NuGet_Delete_InvalidPackageName());
      return FormValidation.ok();
    }

    public FormValidation doCheckPackageVersion(@QueryParameter String value, @QueryParameter String packageName) {
      value = Util.fixEmptyAndTrim(value);
      // TODO: Maybe do some basic semantic version validation?
      if (value != null && value.split(" \r\t\n", 2).length != 1)
        return FormValidation.error(Messages.NuGet_Delete_InvalidPackageVersion());
      packageName = Util.fixEmptyAndTrim(packageName);
      if (packageName == null && value != null)
        return FormValidation.warning(Messages.NuGet_Delete_PackageVersionWithoutName());
      if (packageName != null && value == null)
        return FormValidation.error(Messages.NuGet_Delete_PackageNameWithoutVersion());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillApiKeyIdItems(@CheckForNull @AncestorInPath Jenkins context) {
      return DotNetUtils.getStringCredentialsList(context, true);
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
