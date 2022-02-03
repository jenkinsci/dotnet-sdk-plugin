package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelExpression;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolInstallerDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.data.Downloads;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.net.URL;

/** A tool installer for downloading .NET SDK installation packages from {@code microsoft.com}. */
public final class DotNetSDKInstaller extends ToolInstaller {

  /**
   * Creates a new .NET SDK installer.
   *
   * @param label A label expression identifying the agent(s) for which the installer is suitable.
   */
  @DataBoundConstructor
  public DotNetSDKInstaller(@CheckForNull String label) {
    super(label);
  }

  /**
   * Performs the installation for a .NET SDK, if not already done.
   *
   * @param tool The SDK to install.
   * @param node The agent on which the SDK should be installed.
   * @param log  The task listener to use for output.
   *
   * @return The SDK's installation location.
   *
   * @throws IOException          When an I/O error occurs during processing.
   * @throws InterruptedException When processing is interrupted.
   */
  @Override
  @NonNull
  public FilePath performInstallation(@NonNull ToolInstallation tool, @NonNull Node node, @NonNull TaskListener log) throws IOException, InterruptedException {
    final FilePath dir = this.preferredLocation(tool, node);
    {
      final FilePath marker = dir.child(".installedFrom");
      // FIXME: readToString() does not allow specifying an encoding, but we wrote in UTF-8 specifically.
      if (marker.exists() && marker.readToString().equals(this.url))
        return dir;
    }
    // Unfortunately this processing does not include a means of checking a hash of the archive
    if (dir.installIfNecessaryFrom(new URL(this.url), log, Messages.DotNetSDKInstaller_Installing(this.url, dir, node.getDisplayName()))) {
      dir.child(".timestamp").delete();
      dir.child(".installedFrom").write(this.url, "UTF-8");
      // For now, this does no include ZipExtractionInstaller's behaviour of making everything executable (in part because it's
      // not exposed, so I can't directly reuse it).
    }
    return dir;
  }

  //region Properties

  private boolean includePreview;

  /**
   * Determines whether .NET preview releases should be made available for installation.
   *
   * @return {@code true} if installation of .NET preview releases is allowed, {@code false} otherwise.
   */
  public boolean isIncludePreview() {
    return this.includePreview;
  }

  /**
   * Determines whether .NET preview releases should be made available for installation.
   *
   * @param includePreview {@code true} to allow installation of .NET preview releases, {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIncludePreview(boolean includePreview) {
    this.includePreview = includePreview;
  }

  private String release;

  /**
   * Gets the name of the .NET release containing the SDK to install.
   *
   * @return The name of the .NET release containing the SDK to install.
   */
  @CheckForNull
  public String getRelease() {
    return this.release;
  }

  /**
   * Sets the name of the .NET release containing the SDK to install.
   *
   * @param release The name of the .NET release containing the SDK to install.
   */
  @DataBoundSetter
  public void setRelease(String release) {
    this.release = release;
  }

  private String sdk;

  /**
   * Gets the name of the SDK to install.
   *
   * @return The name of the SDK to install.
   */
  public String getSdk() {
    return this.sdk;
  }

  /**
   * Sets the name of the SDK to install.
   *
   * @param sdk The name of the SDK to install.
   */
  @DataBoundSetter
  public void setSdk(String sdk) {
    this.sdk = sdk;
  }

  private String url;

  /**
   * Gets the URL for the download package of the SDK to install.
   *
   * @return The URL for the download package of the SDK to install.
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Sets the URL for the download package of the SDK to install.
   *
   * @param url The URL for the download package of the SDK to install.
   */
  @DataBoundSetter
  public void setUrl(String url) {
    this.url = url;
  }

  private String version;

  /**
   * Gets the name of the .NET version containing the SDK to install.
   *
   * @return The name of the .NET version containing the SDK to install.
   */
  public String getVersion() {
    return this.version;
  }

  /**
   * Sets the name of the .NET version containing the SDK to install.
   *
   * @param version The name of the .NET version containing the SDK to install.
   */
  @DataBoundSetter
  public void setVersion(String version) {
    this.version = version;
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for a .NET SDK installer. */
  @Extension
  @Symbol("installDotNetSDK")
  public static final class DescriptorImpl extends ToolInstallerDescriptor<DotNetSDKInstaller> {

    private ListBoxModel createList() {
      final ListBoxModel model = new ListBoxModel();
      // Setting the value to null does not work - it causes the validation routines to get '"null"', not 'null', as value.
      model.add(Messages.DotNetSDKInstaller_NotSelected(), "");
      return model;
    }

    /**
     * Performs auto-completion on a label expression.
     *
     * @param value The (partial) label expression to auto-complete.
     *
     * @return The computed auto-completion candidates.
     */
    @SuppressWarnings("unused")
    @NonNull
    public AutoCompletionCandidates doAutoCompleteLabel(@CheckForNull @QueryParameter String value) {
      return LabelExpression.autoComplete(value);
    }

    /**
     * Performs validation on a label expression.
     *
     * @param value The label expression to validate.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckLabel(@CheckForNull @QueryParameter String value) {
      return LabelExpression.validate(value, null);
    }

    /**
     * Performs validation on a .NET release name.
     *
     * @param version The name of the .NET version containing the release.
     * @param value   The .NET release name to validate.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckRelease(@CheckForNull @QueryParameter String version, @CheckForNull @QueryParameter String value) {
      if (Util.fixEmpty(version) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_VersionRequired());
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      final Downloads.Release release = Downloads.getInstance().getRelease(version, value);
      if (release == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidRelease(value, version));
      final String releaseNotes = release.getReleaseNotesLink();
      if (releaseNotes != null)
        return FormValidation.okWithMarkup(releaseNotes);
      return FormValidation.ok();
    }

    /**
     * Performs validation on a .NET SDK name.
     *
     * @param version The name of the .NET version containing the release.
     * @param release The name of the .NET release containing the SDK.
     * @param value   The .NET SDK name to validate.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckSdk(@CheckForNull @QueryParameter String version, @CheckForNull @QueryParameter String release, @CheckForNull @QueryParameter String value) {
      if (Util.fixEmpty(version) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_VersionRequired());
      if (Util.fixEmpty(release) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_ReleaseRequired());
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      final Downloads.Sdk sdk = Downloads.getInstance().getSdk(version, release, value);
      if (sdk == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidSdk(value, version, release));
      if (sdk.info != null)
        return FormValidation.ok(sdk.info);
      return FormValidation.ok();
    }

    /**
     * Performs validation on a .NET SDK installation package URL.
     *
     * @param version The name of the .NET version containing the release.
     * @param release The name of the .NET release containing the SDK.
     * @param sdk     The name of the .NET SDK containing the installation package.
     * @param value   The installation package URL to validate.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckUrl(@CheckForNull @QueryParameter String version, @CheckForNull @QueryParameter String release, @CheckForNull @QueryParameter String sdk, @CheckForNull @QueryParameter String value) {
      if (Util.fixEmpty(version) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_VersionRequired());
      if (Util.fixEmpty(release) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_ReleaseRequired());
      if (Util.fixEmpty(sdk) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_SdkRequired());
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      final Downloads.Package pkg = Downloads.getInstance().getPackage(version, release, sdk, value);
      if (pkg == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidPlatform(version, release, sdk));
      return FormValidation.okWithMarkup(pkg.getDirectDownloadLink());
    }

    /**
     * Performs validation on a .NET version name.
     *
     * @param value The .NET version name to validate.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    public FormValidation doCheckVersion(@CheckForNull @QueryParameter String value) {
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      if (Downloads.getInstance().getVersion(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidVersion(value));
      return FormValidation.ok();
    }

    /**
     * Fills a listbox with the installation package URLs for a .NET SDK.
     *
     * @param sdk The name of the .NET SDK from which to obtain installation package URLs.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillUrlItems(@CheckForNull @QueryParameter String sdk) {
      return Downloads.getInstance().addPackages(this.createList(), sdk);
    }

    /**
     * Fills a listbox with the names of .NET releases.
     *
     * @param version        The name of the .NET version containing the releases.
     * @param includePreview Indicates whether preview releases should be included.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillReleaseItems(@CheckForNull @QueryParameter String version, @QueryParameter boolean includePreview) {
      return Downloads.getInstance().addReleases(this.createList(), version, includePreview);
    }

    /**
     * Fills a listbox with the names of .NET SDKs.
     *
     * @param version The name of the .NET version containing the release.
     * @param release The name of the .NET release containing the SDKs.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillSdkItems(@CheckForNull @QueryParameter String version, @CheckForNull @QueryParameter String release) {
      return Downloads.getInstance().addSdks(this.createList(), version, release);
    }

    /**
     * Fills a listbox with the names of the available .NET versions.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillVersionItems() {
      return Downloads.getInstance().addVersions(this.createList());
    }

    /**
     * Returns the display name for a .NET SDK installer.
     *
     * @return "Install from microsoft.com" or a localized equivalent.
     */
    @Override
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetSDKInstaller_DisplayName();
    }

    /**
     * Determines whether this installer is applicable for the specified type of tool.
     *
     * @param toolType The type of tool to install.
     *
     * @return {@code true} if {@code toolType} is {@link DotNetSDK}; {@code false} otherwise.
     */
    @Override
    public boolean isApplicable(@CheckForNull Class<? extends ToolInstallation> toolType) {
      return toolType == DotNetSDK.class;
    }

  }

  //endregion

}
