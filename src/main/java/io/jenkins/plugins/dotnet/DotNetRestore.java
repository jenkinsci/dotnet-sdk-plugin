package io.jenkins.plugins.dotnet;

import hudson.Extension;
import hudson.Util;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Set;

/** A build step using the 'dotnet' executable to restore dependencies for a project. */
public final class DotNetRestore extends DotNet {

  @DataBoundConstructor
  public DotNetRestore() {
  }

  @Override
  protected void addCommandLineArguments(@Nonnull ArgumentListBuilder args, @Nonnull VariableResolver<String> resolver, @Nonnull Set<String> sensitive) {
    args.add("restore");
    args.add(this.project);
    if (this.disableParallel)
      args.add("--disable-parallel");
    if (this.force)
      args.add("--force");
    if (this.forceEvaluate)
      args.add("--force-evaluate");
    if (this.ignoreFailedSources)
      args.add("--ignore-failed-sources");
    if (this.lockFilePath != null)
      args.add("--lock-file-path", this.lockFilePath);
    if (this.lockedMode)
      args.add("--locked-mode");
    if (this.noCache)
      args.add("--no-cache");
    if (this.noDependencies)
      args.add("--no-dependencies");
    if (this.packages != null)
      args.add("--packages", this.packages);
    if (this.runtimes != null) {
      for (final String runtime : this.runtimes.split(" "))
        args.add("-r:" + runtime);
    }
    if (this.sources != null) {
      for (final String source : this.sources.split(" "))
        args.add("-s:" + source);
    }
    if (this.useLockFile)
      args.add("--use-lock-file");
    if (this.verbosity != null)
      args.add("-v:" + this.verbosity);
  }

  //region Properties

  private boolean disableParallel;

  public boolean isDisableParallel() {
    return this.disableParallel;
  }

  @DataBoundSetter
  public void setDisableParallel(boolean disableParallel) {
    this.disableParallel = disableParallel;
  }

  private boolean force;

  public boolean isForce() {
    return this.force;
  }

  @DataBoundSetter
  public void setForce(boolean force) {
    this.force = force;
  }

  private boolean forceEvaluate;

  public boolean isForceEvaluate() {
    return this.forceEvaluate;
  }

  @DataBoundSetter
  public void setForceEvaluate(boolean forceEvaluate) {
    this.forceEvaluate = forceEvaluate;
  }

  private boolean ignoreFailedSources;

  public boolean isIgnoreFailedSources() {
    return this.ignoreFailedSources;
  }

  @DataBoundSetter
  public void setIgnoreFailedSources(boolean ignoreFailedSources) {
    this.ignoreFailedSources = ignoreFailedSources;
  }

  private String lockFilePath;

  public String getLockFilePath() {
    return this.lockFilePath;
  }

  @DataBoundSetter
  public void setLockFilePath(String lockFilePath) {
    this.lockFilePath = Util.fixEmptyAndTrim(lockFilePath);
  }

  private boolean lockedMode;

  public boolean isLockedMode() {
    return this.lockedMode;
  }

  @DataBoundSetter
  public void setLockedMode(boolean lockedMode) {
    this.lockedMode = lockedMode;
  }

  private boolean noCache;

  public boolean isNoCache() {
    return this.noCache;
  }

  @DataBoundSetter
  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  private boolean noDependencies;

  public boolean isNoDependencies() {
    return this.noDependencies;
  }

  @DataBoundSetter
  public void setNoDependencies(boolean noDependencies) {
    this.noDependencies = noDependencies;
  }

  private String packages;

  public String getPackages() {
    return this.packages;
  }

  @DataBoundSetter
  public void setPackages(String packages) {
    this.packages = Util.fixEmptyAndTrim(packages);
  }

  protected String project;

  public String getProject() {
    return this.project;
  }

  @DataBoundSetter
  public void setProject(String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  private String runtimes;

  public String getRuntimes() {
    return this.runtimes;
  }

  @DataBoundSetter
  public void setRuntimes(String runtimes) {
    this.runtimes = DotNetUtils.normalizeList(runtimes);
  }

  private String sources;

  public String getSources() {
    return this.sources;
  }

  @DataBoundSetter
  public void setSources(String sources) {
    this.sources = DotNetUtils.normalizeList(sources);
  }

  private boolean useLockFile;

  public boolean isUseLockFile() {
    return this.useLockFile;
  }

  @DataBoundSetter
  public void setUseLockFile(boolean useLockFile) {
    this.useLockFile = useLockFile;
  }

  private String verbosity;

  public String getVerbosity() {
    return this.verbosity;
  }

  @DataBoundSetter
  public void setVerbosity(String verbosity) {
    this.verbosity = Util.fixEmptyAndTrim(verbosity);
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetRestore")
  public static class DescriptorImpl extends DotNet.DescriptorImpl {

    public DescriptorImpl() {
      this.load();
    }

    protected DescriptorImpl(Class<? extends DotNetRestore> clazz) {
      super(clazz);
    }

    @Nonnull
    public String getDisplayName() {
      return Messages.DotNetRestore_DisplayName();
    }

  }

  //endregion

}
