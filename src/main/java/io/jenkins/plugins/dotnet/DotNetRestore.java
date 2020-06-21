package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/** A build step to run "{@code dotnet restore}", restoring packages for a project. */
public final class DotNetRestore extends DotNet {

  /** Creates a new "{@code dotnet restore}" build step. */
  @DataBoundConstructor
  public DotNetRestore() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet restore}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code restore}</li>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>{@code --disable-parallel}, if requested via {@link #setDisableParallel(boolean)}.</li>
   *   <li>{@code --force}, if requested via {@link #setForce(boolean)}.</li>
   *   <li>{@code --force-evaluate}, if requested via {@link #setForceEvaluate(boolean)}.</li>
   *   <li>{@code --ignore-failed-sources}, if requested via {@link #setIgnoreFailedSources(boolean)}.</li>
   *   <li>{@code --lock-file-path xxx}, if a lock file path was specified via {@link #setLockFilePath(String)}.</li>
   *   <li>{@code --locked-mode}, if requested via {@link #setLockedMode(boolean)}.</li>
   *   <li>{@code --no-cache}, if requested via {@link #setNoCache(boolean)}.</li>
   *   <li>{@code --no-dependencies}, if requested via {@link #setNoDependencies(boolean)}.</li>
   *   <li>{@code --packages xxx}, if a package directory was specified via {@link #setPackages(String)}.</li>
   *   <li>{@code -r:xxx} for each runtime identifier specified via {@link #setRuntimes(String)}.</li>
   *   <li>{@code -s:xxx} for each source specified via {@link #setSources(String)}.</li>
   *   <li>{@code --use-lock-file}, if requested via {@link #setUseLockFile(boolean)}.</li>
   *   <li>{@code -v:xxx}, if a verbosity has been specified via {@link #setVerbosity(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
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

  /**
   * Determines whether or not multiple projects can be restored in parallel.
   *
   * @return {@code true} when multiple projects are restored one by one; {@code false} otherwise.
   */
  public boolean isDisableParallel() {
    return this.disableParallel;
  }

  /**
   * Determines whether or not multiple projects can be restored in parallel.
   *
   * @param disableParallel {@code true} to restore multiple projects one by one; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setDisableParallel(boolean disableParallel) {
    this.disableParallel = disableParallel;
  }

  private boolean force;

  /**
   * Determines whether or not dependency resolution should be forced.
   *
   * @return {@code true} when all dependencies should be resolved even if the last restore was successful; {@code false} otherwise.
   */
  public boolean isForce() {
    return this.force;
  }

  /**
   * Determines whether or not dependency resolution should be forced.
   *
   * @param force {@code true} to resolve all dependencies even if the last restore was successful; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setForce(boolean force) {
    this.force = force;
  }

  private boolean forceEvaluate;

  /**
   * Determines whether or not all dependencies should be re-evaluated even when a lock file exists.
   *
   * @return {@code true} when all dependencies are re-evaluated even when a lock file exists; {@code false} otherwise.
   */
  public boolean isForceEvaluate() {
    return this.forceEvaluate;
  }

  /**
   * Determines whether or not all dependencies should be re-evaluated even when a lock file exists.
   *
   * @param forceEvaluate {@code true} to re-evaluate all dependencies even when a lock file exists; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setForceEvaluate(boolean forceEvaluate) {
    this.forceEvaluate = forceEvaluate;
  }

  private boolean ignoreFailedSources;

  /**
   * Determines whether or not failed sources should be ignored.
   *
   * @return {@code true} when failed sources are ignored; {@code false} otherwise.
   */
  public boolean isIgnoreFailedSources() {
    return this.ignoreFailedSources;
  }

  /**
   * Determines whether or not failed sources should be ignored.
   *
   * @param ignoreFailedSources {@code true} to ignore failed sources; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIgnoreFailedSources(boolean ignoreFailedSources) {
    this.ignoreFailedSources = ignoreFailedSources;
  }

  private String lockFilePath;

  /**
   * Gets the path to the lock file.
   *
   * @return The path to the lock file.
   */
  @CheckForNull
  public String getLockFilePath() {
    return this.lockFilePath;
  }

  /**
   * Sets the path to the lock file.
   *
   * @param lockFilePath The path to the lock file.
   */
  @DataBoundSetter
  public void setLockFilePath(@CheckForNull String lockFilePath) {
    this.lockFilePath = Util.fixEmptyAndTrim(lockFilePath);
  }

  private boolean lockedMode;

  /**
   * Determines whether or not updating the project lock file is allowed.
   *
   * @return {@code true} when updating the project lock file is not allowed; {@code false} otherwise.
   */
  public boolean isLockedMode() {
    return this.lockedMode;
  }

  /**
   * Determines whether or not updating the project lock file is allowed.
   *
   * @param lockedMode {@code true} to disallow updating the project lock file; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setLockedMode(boolean lockedMode) {
    this.lockedMode = lockedMode;
  }

  private boolean noCache;

  /**
   * Determines whether or not HTTP requests should be cached.
   *
   * @return {@code true} when HTTP requests are not cached; {@code false} otherwise.
   */
  public boolean isNoCache() {
    return this.noCache;
  }

  /**
   * Determines whether or not HTTP requests should be cached.
   *
   * @param noCache {@code true} not to cache HTTP requests; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  private boolean noDependencies;

  /**
   * Determines whether or not to ignore project-to-project dependencies.
   *
   * @return {@code true} when project-to-project dependencies are ignored; {@code false} otherwise.
   */
  public boolean isNoDependencies() {
    return this.noDependencies;
  }

  /**
   * Determines whether or not to ignore project-to-project dependencies.
   *
   * @param noDependencies {@code true} when project-to-project dependencies should be ignored; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoDependencies(boolean noDependencies) {
    this.noDependencies = noDependencies;
  }

  private String packages;

  /**
   * Gets the directory to use for restored packages.
   *
   * @return The directory to use for restored packages.
   */
  @CheckForNull
  public String getPackages() {
    return this.packages;
  }

  /**
   * Sets the directory to use for restored packages.
   *
   * @param packages The directory to use for restored packages.
   */
  @DataBoundSetter
  public void setPackages(@CheckForNull String packages) {
    this.packages = Util.fixEmptyAndTrim(packages);
  }

  protected String project;

  /**
   * Gets the project to restore.
   *
   * @return The project to restore.
   */
  @CheckForNull
  public String getProject() {
    return this.project;
  }

  /**
   * Sets the project to restore.
   *
   * @param project The project to restore.
   */
  @DataBoundSetter
  public void setProject(@CheckForNull String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  private String runtimes;

  /**
   * Gets the runtime identifiers to use.
   *
   * @return The runtime identifiers to use.
   */
  @CheckForNull
  public String getRuntimes() {
    return this.runtimes;
  }

  /**
   * Sets the runtime identifiers to use.
   *
   * @param runtimes The runtime identifiers to use.
   */
  @DataBoundSetter
  public void setRuntimes(@CheckForNull String runtimes) {
    this.runtimes = DotNetUtils.normalizeList(runtimes);
  }

  private String sources;

  /**
   * Gets the package sources to use.
   *
   * @return The package sources to use.
   */
  @CheckForNull
  public String getSources() {
    return this.sources;
  }

  /**
   * Sets the package sources to use.
   *
   * @param sources The package sources to use.
   */
  @DataBoundSetter
  public void setSources(@CheckForNull String sources) {
    this.sources = DotNetUtils.normalizeList(sources);
  }

  private boolean useLockFile;

  /**
   * Determines whether or not a project lock file should be generated and used.
   *
   * @return {@code true} when a project lock file is generated and used; {@code false} otherwise.
   */
  public boolean isUseLockFile() {
    return this.useLockFile;
  }

  /**
   * Determines whether or not a project lock file should be generated and used.
   *
   * @param useLockFile {@code true} to generate and use a project lock file; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setUseLockFile(boolean useLockFile) {
    this.useLockFile = useLockFile;
  }

  private String verbosity;

  /**
   * Gets the verbosity to use for the command.
   *
   * @return The verbosity to use for the command.
   */
  @CheckForNull
  public String getVerbosity() {
    return this.verbosity;
  }

  /**
   * Sets the verbosity to use for the command.
   *
   * @param verbosity The verbosity to use for the command.
   */
  @DataBoundSetter
  public void setVerbosity(@CheckForNull String verbosity) {
    this.verbosity = Util.fixEmptyAndTrim(verbosity);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet restore}" build steps. */
  @Extension
  @Symbol("dotnetRestore")
  public static final class DescriptorImpl extends CommandDescriptor {

    /** Creates a new "{@code dotnet restore}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetRestore_DisplayName();
    }

  }

  //endregion

}
