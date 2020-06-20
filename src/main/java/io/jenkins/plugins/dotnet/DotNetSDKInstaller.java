package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.model.TaskListener;
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

public final class DotNetSDKInstaller extends ToolInstaller {

  @DataBoundConstructor
  public DotNetSDKInstaller(String label) {
    super(label);
  }

  @Override
  public FilePath performInstallation(ToolInstallation tool, Node node, TaskListener log) throws IOException, InterruptedException {
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
      dir.child(".installedFrom").write(this.url,"UTF-8");
      // For now, this does no include ZipExtractionInstaller's behaviour of making everything executable (in part because it's
      // not exposed, so I can't directly reuse it).
    }
    return dir;
  }

  //region Properties

  private boolean includePreview;

  public boolean isIncludePreview() {
    return this.includePreview;
  }

  @DataBoundSetter
  public void setIncludePreview(boolean includePreview) {
    this.includePreview = includePreview;
  }

  private String release;

  public String getRelease() {
    return this.release;
  }

  @DataBoundSetter
  public void setRelease(String release) {
    this.release = release;
  }

  private String sdk;

  public String getSdk() {
    return this.sdk;
  }

  @DataBoundSetter
  public void setSdk(String sdk) {
    this.sdk = sdk;
  }

  private String url;

  public String getUrl() {
    return this.url;
  }

  @DataBoundSetter
  public void setUrl(String url) {
    this.url = url;
  }

  private String version;

  public String getVersion() {
    return this.version;
  }

  @DataBoundSetter
  public void setVersion(String version) {
    this.version = version;
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("installDotNetSDK")
  public static final class DescriptorImpl extends ToolInstallerDescriptor<DotNetSDKInstaller> {

    private ListBoxModel createList() {
      final ListBoxModel model = new ListBoxModel();
      // Setting the value to null does not work - it causes the validation routines to get '"null"', not 'null', as value.
      model.add(Messages.DotNetSDKInstaller_NotSelected(), "");
      return model;
    }

    @SuppressWarnings("unused")
    public AutoCompletionCandidates doAutoCompleteLabel(@QueryParameter String value) {
      // JENKINS-26097: There is no available static method for Label completion
      return new FreeStyleProject.DescriptorImpl().doAutoCompleteLabel(value);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckLabel(@QueryParameter String value) {
      return AbstractProject.AbstractProjectDescriptor.validateLabelExpression(value, null);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckRelease(@QueryParameter String version, @QueryParameter String value) {
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

    @SuppressWarnings("unused")
    public FormValidation doCheckSdk(@QueryParameter String version, @QueryParameter String release, @QueryParameter String value) {
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

    @SuppressWarnings("unused")
    public FormValidation doCheckUrl(@QueryParameter String version, @QueryParameter String release, @QueryParameter String sdk, @QueryParameter String value) {
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

    @SuppressWarnings("unused")
    public FormValidation doCheckVersion(@QueryParameter String value) {
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      if (Downloads.getInstance().getVersion(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidVersion(value));
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillUrlItems(@QueryParameter String sdk) {
      return Downloads.getInstance().addPackages(this.createList(), sdk);
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillReleaseItems(@QueryParameter String version, @QueryParameter boolean includePreview) {
      return Downloads.getInstance().addReleases(this.createList(), version, includePreview);
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillSdkItems(@QueryParameter String version, @QueryParameter String release) {
      return Downloads.getInstance().addSdks(this.createList(), version, release);
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillVersionItems() {
      return Downloads.getInstance().addVersions(this.createList());
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.DotNetSDKInstaller_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
      return toolType == DotNetSDK.class;
    }

  }

  //endregion

}
