package io.jenkins.plugins.dotnet;

import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
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

import javax.annotation.Nonnull;
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
    if (dir.installIfNecessaryFrom(new URL(this.url), log, Messages.DotNetSDKInstaller_Installing(this.url, dir, node.getDisplayName()))) {
      dir.child(".timestamp").delete();
      dir.child(".installedFrom").write(this.url,"UTF-8");
      // For now, this does no include ZipExtractionInstaller's behaviour of making everything executable (in part because it's
      // not exposed, so I can't directly reuse it).
      // TODO: Maybe save the sdk name, so that it does not need to be looked up (in the context of creating/updating global.json
      // TODO: to force the use of an exact SDK version).
    }
    // For the time being, this does not replicate
    return dir;
  }

  //region Properties

  private boolean includeEndOfLife;

  public boolean isIncludeEndOfLife() {
    return this.includeEndOfLife;
  }

  @DataBoundSetter
  public void setIncludeEndOfLife(boolean includeEndOfLife) {
    this.includeEndOfLife = includeEndOfLife;
  }

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

    public FormValidation doCheckRelease(@QueryParameter String version, @QueryParameter String release, @QueryParameter String value) {
      if (Util.fixEmpty(version) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_VersionRequired());
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      if (Downloads.getInstance().getRelease(version, value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidRelease(value, version));
      return FormValidation.ok();
    }

    public FormValidation doCheckSdk(@QueryParameter String version, @QueryParameter String release, @QueryParameter String value) {
      if (Util.fixEmpty(version) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_VersionRequired());
      if (Util.fixEmpty(release) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_ReleaseRequired());
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      if (Downloads.getInstance().getSdk(version, release, value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidSdk(value, version, release));
      return FormValidation.ok();
    }

    public FormValidation doCheckUrl(@QueryParameter String version, @QueryParameter String release, @QueryParameter String sdk, @QueryParameter String value) {
      if (Util.fixEmpty(version) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_VersionRequired());
      if (Util.fixEmpty(release) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_ReleaseRequired());
      if (Util.fixEmpty(sdk) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_SdkRequired());
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      if (Downloads.getInstance().getPackage(version, release, sdk, value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidPlatform(version, release, sdk));
      return FormValidation.ok();
    }

    public FormValidation doCheckVersion(@QueryParameter String value) {
      if (Util.fixEmpty(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_Required());
      if (Downloads.getInstance().getVersion(value) == null)
        return FormValidation.error(Messages.DotNetSDKInstaller_InvalidVersion(value));
      return FormValidation.ok();
    }

    public ListBoxModel doFillUrlItems(@QueryParameter String version, @QueryParameter String release, @QueryParameter String sdk) {
      return Downloads.getInstance().addPackages(this.createList(), version, release, sdk);
    }

    public ListBoxModel doFillReleaseItems(@QueryParameter String version, @QueryParameter boolean includePreview) {
      return Downloads.getInstance().addReleases(this.createList(), version, includePreview);
    }

    public ListBoxModel doFillSdkItems(@QueryParameter String version, @QueryParameter String release) {
      return Downloads.getInstance().addSdks(this.createList(), version, release);
    }

    public ListBoxModel doFillVersionItems(@QueryParameter boolean includePreview, @QueryParameter boolean includeEndOfLife) {
      return Downloads.getInstance().addVersions(this.createList(), includePreview, includeEndOfLife);
    }

    @Nonnull
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

  //region SDK Downloads Data Structure

  //endregion

}
