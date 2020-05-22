package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.List;

/** A build step using the 'dotnet' executable to create a NuGet package for a project. */
public final class DotNetPack extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetPack() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("pack");
    super.addCommandLineArguments(args);
    if (this.force)
      args.add("--force");
    if (this.noBuild)
      args.add("--no-build");
    if (this.noDependencies)
      args.add("--no-dependencies");
    if (this.noRestore)
      args.add("--no-restore");
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
    if (this.includeSource)
      args.add("--include-source");
    if (this.includeSymbols)
      args.add("--include-symbols");
    if (this.versionSuffix != null) {
      args.add("--version-suffix");
      args.add(this.versionSuffix);
    }
  }

  //region Properties

  private boolean force;

  public boolean isForce() {
    return this.force;
  }

  @DataBoundSetter
  public void setForce(boolean force) {
    this.force = force;
  }

  private boolean includeSource;

  public boolean isIncludeSource() {
    return this.includeSource;
  }

  @DataBoundSetter
  public void setIncludeSource(boolean includeSource) {
    this.includeSource = includeSource;
  }

  private boolean includeSymbols;

  public boolean isIncludeSymbols() {
    return this.includeSymbols;
  }

  @DataBoundSetter
  public void setIncludeSymbols(boolean includeSymbols) {
    this.includeSymbols = includeSymbols;
  }

  private boolean noBuild;

  public boolean isNoBuild() {
    return this.noBuild;
  }

  @DataBoundSetter
  public void setNoBuild(boolean noBuild) {
    this.noBuild = noBuild;
  }

  private boolean noDependencies;

  public boolean isNoDependencies() {
    return this.noDependencies;
  }

  @DataBoundSetter
  public void setNoDependencies(boolean noDependencies) {
    this.noDependencies = noDependencies;
  }

  private boolean noRestore;

  public boolean isNoRestore() {
    return this.noRestore;
  }

  @DataBoundSetter
  public void setNoRestore(boolean noRestore) {
    this.noRestore = noRestore;
  }

  private String runtime;

  public String getRuntime() {
    return this.runtime;
  }

  @DataBoundSetter
  public void setRuntime(String runtime) {
    this.runtime = Util.fixEmptyAndTrim(runtime);
  }

  private String versionSuffix;

  public String getVersionSuffix() {
    return this.versionSuffix;
  }

  @DataBoundSetter
  public void setVersionSuffix(String versionSuffix) {
    this.versionSuffix = Util.fixEmptyAndTrim(versionSuffix);
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetPack")
  public static class DescriptorImpl extends DotNetUsingMSBuild.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetPack> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetPack_DisplayName();
    }

  }

  //endregion

}
