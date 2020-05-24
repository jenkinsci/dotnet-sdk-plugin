package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.CheckForNull;
import java.util.List;

/** A build step using the 'dotnet' executable to list all resolved package dependencies for a project. */
public final class DotNetListPackage extends DotNet {

  @DataBoundConstructor
  public DotNetListPackage() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("list");
    if (this.project != null)
      args.add(this.project);
    args.add("package");
    if (this.deprecated)
      args.add("--deprecated");
    if (this.outdated)
      args.add("--outdated");
    if (this.frameworks != null) {
      for (String fmw : this.frameworks.split(" ")) {
        args.add("--framework");
        args.add(fmw);
      }
    }
    if (this.includeTransitive)
      args.add("--include-transitive");
    if (this.outdated || this.deprecated) {
      if (this.includePreRelease)
        args.add("--include-prerelease");
      if (this.highestMinor)
        args.add("--highest-minor");
      if (this.highestPatch)
        args.add("--highest-patch");
      if (this.config != null) {
        args.add("--config");
        args.add(this.config);
      }
      if (this.sources != null) {
        for (String src : this.sources.split(" ")) {
          args.add("--source");
          args.add(src);
        }
      }
    }
  }

  //region Properties

  private String config;

  public String getConfig() {
    return this.config;
  }

  @DataBoundSetter
  public void setConfig(String config) {
    this.config = Util.fixEmptyAndTrim(config);
  }

  private boolean deprecated;

  public boolean isDeprecated() {
    return this.deprecated;
  }

  @DataBoundSetter
  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }

  private String frameworks;

  public String getFrameworks() {
    return this.frameworks;
  }

  @DataBoundSetter
  public void setFrameworks(String frameworks) {
    this.frameworks = DotNetUtils.normalizeList(frameworks);
  }

  private boolean highestMinor;

  public boolean isHighestMinor() {
    return this.highestMinor;
  }

  @DataBoundSetter
  public void setHighestMinor(boolean highestMinor) {
    this.highestMinor = highestMinor;
  }

  private boolean highestPatch;

  public boolean isHighestPatch() {
    return this.highestPatch;
  }

  @DataBoundSetter
  public void setHighestPatch(boolean highestPatch) {
    this.highestPatch = highestPatch;
  }

  private boolean includePreRelease;

  public boolean isIncludePreRelease() {
    return this.includePreRelease;
  }

  @DataBoundSetter
  public void setIncludePreRelease(boolean includePreRelease) {
    this.includePreRelease = includePreRelease;
  }

  private boolean includeTransitive;

  public boolean isIncludeTransitive() {
    return this.includeTransitive;
  }

  @DataBoundSetter
  public void setIncludeTransitive(boolean includeTransitive) {
    this.includeTransitive = includeTransitive;
  }

  private boolean outdated;

  public boolean isOutdated() {
    return this.outdated;
  }

  @DataBoundSetter
  public void setOutdated(boolean outdated) {
    this.outdated = outdated;
  }

  private String project;

  public String getProject() {
    return this.project;
  }

  @DataBoundSetter
  public void setProject(String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  private String sources;

  public String getSources() {
    return this.sources;
  }

  @DataBoundSetter
  public void setSources(String sources) {
    this.sources = DotNetUtils.normalizeList(sources);
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetListPackage")
  public static class DescriptorImpl extends DotNet.DescriptorImpl {

    public DescriptorImpl() {
      this.load();
    }

    protected DescriptorImpl(Class<? extends DotNetListPackage> clazz) {
      super(clazz);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckConfig(@QueryParameter String value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (Util.fixEmptyAndTrim(value) != null && !deprecated && !outdated)
        return FormValidation.warning(Messages.DotNetListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckDeprecated(@QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (deprecated && outdated)
        return FormValidation.error(Messages.DotNetListPackage_EitherDeprecatedOrOutdated());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckHighestMinor(@QueryParameter boolean value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (value && !deprecated && !outdated)
        return FormValidation.warning(Messages.DotNetListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckHighestPatch(@QueryParameter boolean value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (value && !deprecated && !outdated)
        return FormValidation.warning(Messages.DotNetListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckIncludePreRelease(@QueryParameter boolean value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (value && !deprecated && !outdated)
        return FormValidation.warning(Messages.DotNetListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckOutdated(@QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (deprecated && outdated)
        return FormValidation.error(Messages.DotNetListPackage_EitherDeprecatedOrOutdated());
      return FormValidation.ok();
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckSources(@QueryParameter String value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (Util.fixEmptyAndTrim(value) != null && !deprecated && !outdated)
        return FormValidation.warning(Messages.DotNetListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetListPackage_DisplayName();
    }

  }

  //endregion

}
