package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.List;

/** A build step using the 'dotnet' executable to build a project. */
public final class DotNetBuild extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetBuild() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("build");
    super.addCommandLineArguments(args);
    if (this.force)
      args.add("--force");
    if (this.noDependencies)
      args.add("--no-dependencies");
    if (this.noIncremental)
      args.add("--no-incremental");
    if (this.noRestore)
      args.add("--no-restore");
    if (this.framework != null)
      args.add("-f:" + this.framework);
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
    if (this.targets != null) {
      for (final String target : this.targets.split(" "))
        args.add("-t:" + target);
    }
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

  private String framework;

  public String getFramework() {
    return this.framework;
  }

  @DataBoundSetter
  public void setFramework(String framework) {
    this.framework = Util.fixEmptyAndTrim(framework);
  }

  private boolean noDependencies;

  public boolean isNoDependencies() {
    return this.noDependencies;
  }

  @DataBoundSetter
  public void setNoDependencies(boolean noDependencies) {
    this.noDependencies = noDependencies;
  }

  private boolean noIncremental;

  public boolean isNoIncremental() {
    return this.noIncremental;
  }

  @DataBoundSetter
  public void setNoIncremental(boolean noIncremental) {
    this.noIncremental = noIncremental;
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

  private String targets;

  public String getTargets() {
    return this.targets;
  }

  @DataBoundSetter
  public void setTargets(String targets) {
    this.targets = DotNetUtils.normalizeList(targets);
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
  @Symbol("dotnetBuild")
  public static class DescriptorImpl extends DotNetUsingMSBuild.DescriptorImpl {

    public DescriptorImpl() {
      this.load();
    }

    protected DescriptorImpl(Class<? extends DotNetBuild> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetBuild_DisplayName();
    }

  }

  //endregion

}
