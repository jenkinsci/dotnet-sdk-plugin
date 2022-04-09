package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import io.jenkins.plugins.dotnet.DotNetUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.structs.describable.UninstantiatedDescribable;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.HashMap;
import java.util.Map;

/** A build step to run "{@code dotnet restore}", restoring packages for a project. */
public final class Restore extends Command {

  /** Creates a new "{@code dotnet restore}" build step. */
  @DataBoundConstructor
  public Restore() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet restore}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code restore}</li>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>{@code --configfile xxx}, if a config file was specified via {@link #setConfigfile(String)}.</li>
   *   <li>{@code --disable-parallel}, if requested via {@link #setDisableParallel(boolean)}.</li>
   *   <li>{@code --force}, if requested via {@link #setForce(boolean)}.</li>
   *   <li>{@code --force-evaluate}, if requested via {@link #setForceEvaluate(boolean)}.</li>
   *   <li>{@code --ignore-failed-sources}, if requested via {@link #setIgnoreFailedSources(boolean)}.</li>
   *   <li>{@code --lock-file-path xxx}, if a lock file path was specified via {@link #setLockFilePath(String)}.</li>
   *   <li>{@code --locked-mode}, if requested via {@link #setLockedMode(boolean)}.</li>
   *   <li>{@code --no-cache}, if requested via {@link #setNoCache(boolean)}.</li>
   *   <li>{@code --no-dependencies}, if requested via {@link #setNoDependencies(boolean)}.</li>
   *   <li>{@code --packages xxx}, if a package directory was specified via {@link #setPackages(String)}.</li>
   *   <li>
   *     {@code -r:xxx} for each runtime identifier specified via {@link #setRuntime(String)}, {@link #setRuntimes(String...)} or
   *     {@link #setRuntimesString(String)}.
   *   </li>
   *   <li>
   *     {@code -s:xxx} for each source specified via {@link #setSourcesString(String)}, {@link #setSources(String...)} or
   *     {@link #setSourcesString(String)}.
   *   </li>
   *   <li>{@code --use-lock-file}, if requested via {@link #setUseLockFile(boolean)}.</li>
   *   <li>{@code -v:xxx}, if a verbosity has been specified via {@link #setVerbosity(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) {
    args.add("restore");
    args.addOption(this.project);
    args.addOption("configfile", this.configfile);
    args.addFlag("disable-parallel", this.disableParallel);
    args.addFlag("force", this.force);
    args.addFlag("force-evaluate", this.forceEvaluate);
    args.addFlag("ignore-failed-sources", this.ignoreFailedSources);
    args.addOption("lock-file-path", this.lockFilePath);
    args.addFlag("locked-mode", this.lockedMode);
    args.addFlag("no-cache", this.noCache);
    args.addFlag("no-dependencies", this.noDependencies);
    args.addOption("packages", this.packages);
    args.addOptions('r', this.runtimes);
    args.addOptions('s', this.sources);
    args.addFlag("use-lock-file", this.useLockFile);
    args.addOption('v', this.verbosity);
  }

  //region Properties

  private String configfile;

  /**
   * Gets the NuGet configuration (nuget.config) file to use.
   *
   * @return The NuGet configuration (nuget.config) file to use.
   */
  public String getConfigfile() {
    return this.configfile;
  }

  /**
   * Sets the NuGet configuration (nuget.config) file to use.
   *
   * @param configfile The NuGet configuration (nuget.config) file to use.
   */
  @DataBoundSetter
  public void setConfigfile(String configfile) {
    this.configfile = Util.fixEmptyAndTrim(configfile);
  }

  private boolean disableParallel;

  /**
   * Determines whether multiple projects can be restored in parallel.
   *
   * @return {@code true} when multiple projects are restored one by one; {@code false} otherwise.
   */
  public boolean isDisableParallel() {
    return this.disableParallel;
  }

  /**
   * Determines whether multiple projects can be restored in parallel.
   *
   * @param disableParallel {@code true} to restore multiple projects one by one; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setDisableParallel(boolean disableParallel) {
    this.disableParallel = disableParallel;
  }

  private boolean force;

  /**
   * Determines whether dependency resolution should be forced.
   *
   * @return {@code true} when all dependencies should be resolved even if the last restore was successful; {@code false} otherwise.
   */
  public boolean isForce() {
    return this.force;
  }

  /**
   * Determines whether dependency resolution should be forced.
   *
   * @param force {@code true} to resolve all dependencies even if the last restore was successful; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setForce(boolean force) {
    this.force = force;
  }

  private boolean forceEvaluate;

  /**
   * Determines whether all dependencies should be re-evaluated even when a lock file exists.
   *
   * @return {@code true} when all dependencies are re-evaluated even when a lock file exists; {@code false} otherwise.
   */
  public boolean isForceEvaluate() {
    return this.forceEvaluate;
  }

  /**
   * Determines whether all dependencies should be re-evaluated even when a lock file exists.
   *
   * @param forceEvaluate {@code true} to re-evaluate all dependencies even when a lock file exists; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setForceEvaluate(boolean forceEvaluate) {
    this.forceEvaluate = forceEvaluate;
  }

  private boolean ignoreFailedSources;

  /**
   * Determines whether failed sources should be ignored.
   *
   * @return {@code true} when failed sources are ignored; {@code false} otherwise.
   */
  public boolean isIgnoreFailedSources() {
    return this.ignoreFailedSources;
  }

  /**
   * Determines whether failed sources should be ignored.
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
   * Determines whether updating the project lock file is allowed.
   *
   * @return {@code true} when updating the project lock file is not allowed; {@code false} otherwise.
   */
  public boolean isLockedMode() {
    return this.lockedMode;
  }

  /**
   * Determines whether updating the project lock file is allowed.
   *
   * @param lockedMode {@code true} to disallow updating the project lock file; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setLockedMode(boolean lockedMode) {
    this.lockedMode = lockedMode;
  }

  private boolean noCache;

  /**
   * Determines whether HTTP requests should be cached.
   *
   * @return {@code true} when HTTP requests are not cached; {@code false} otherwise.
   */
  public boolean isNoCache() {
    return this.noCache;
  }

  /**
   * Determines whether HTTP requests should be cached.
   *
   * @param noCache {@code true} not to cache HTTP requests; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  private boolean noDependencies;

  /**
   * Determines whether to ignore project-to-project dependencies.
   *
   * @return {@code true} when project-to-project dependencies are ignored; {@code false} otherwise.
   */
  public boolean isNoDependencies() {
    return this.noDependencies;
  }

  /**
   * Determines whether to ignore project-to-project dependencies.
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
  public String getRuntime() {
    return DotNetUtils.singleToken(this.runtimes);
  }

  /**
   * Sets the sole runtime identifier to use.
   * <p>
   * To set more than one, use {@link #setRuntimes(String...)} instead.
   *
   * @param runtime The sole runtime identifier to use.
   */
  @DataBoundSetter
  public void setRuntime(@CheckForNull String runtime) {
    this.runtimes = DotNetUtils.detokenize(' ', runtime);
  }

  /**
   * Gets the runtime identifiers to use.
   *
   * @return The runtime identifiers to use.
   */
  @CheckForNull
  public String[] getRuntimes() {
    return DotNetUtils.tokenize(this.runtimes);
  }

  /**
   * Sets the runtime identifiers to use.
   *
   * @param runtimes The runtime identifiers to use.
   */
  @DataBoundSetter
  public void setRuntimes(@CheckForNull String... runtimes) {
    this.runtimes = DotNetUtils.detokenize(' ', runtimes);
  }

  /**
   * Gets the runtime identifiers to use.
   *
   * @return The runtime identifiers to use.
   */
  @CheckForNull
  public String getRuntimesString() {
    return this.runtimes;
  }

  /**
   * Sets the runtime identifiers to use.
   *
   * @param runtimes The runtime identifiers to use.
   */
  @DataBoundSetter
  public void setRuntimesString(@CheckForNull String runtimes) {
    this.runtimes = Util.fixEmptyAndTrim(runtimes);
  }

  private String sources;

  /**
   * Gets the sole package source to use.
   *
   * @return The sole package source to use, or {@code null} when there is not exactly one package source set.
   */
  @CheckForNull
  public String getSource() {
    return DotNetUtils.singleToken(this.sources);
  }

  /**
   * Sets the sole package source to use.
   * <p>
   * To set more than one, use {@link #setSources(String...)} instead.
   *
   * @param source The sole package source to use.
   */
  @DataBoundSetter
  public void setSource(@CheckForNull String source) {
    this.sources = DotNetUtils.detokenize(' ', source);
  }

  /**
   * Gets the package sources to use.
   *
   * @return The package sources to use.
   */
  @CheckForNull
  public String[] getSources() {
    return DotNetUtils.tokenize(this.sources);
  }

  /**
   * Sets the package sources to use.
   *
   * @param sources The package sources to use.
   */
  @DataBoundSetter
  public void setSources(@CheckForNull String... sources) {
    this.sources = DotNetUtils.detokenize(' ', sources);
  }

  /**
   * Gets the package sources to use.
   *
   * @return The package sources to use.
   */
  @CheckForNull
  public String getSourcesString() {
    return this.sources;
  }

  /**
   * Sets the package sources to use.
   *
   * @param sources The package sources to use.
   */
  @DataBoundSetter
  public void setSourcesString(@CheckForNull String sources) {
    this.sources = Util.fixEmptyAndTrim(sources);
  }

  private boolean useLockFile;

  /**
   * Determines whether a project lock file should be generated and used.
   *
   * @return {@code true} when a project lock file is generated and used; {@code false} otherwise.
   */
  public boolean isUseLockFile() {
    return this.useLockFile;
  }

  /**
   * Determines whether a project lock file should be generated and used.
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

    @NonNull
    @Override
    public UninstantiatedDescribable customUninstantiate(@NonNull UninstantiatedDescribable ud) {
      ud = super.customUninstantiate(ud);
      final Map<String, ?> oldArgs = ud.getArguments();
      final Map<String, Object> newArgs = new HashMap<>();
      for (final Map.Entry<String, ?> arg : oldArgs.entrySet()) {
        final String name = arg.getKey();
        if ("runtimes".equals(name) && oldArgs.containsKey("runtime")) {
          continue;
        }
        if ("runtimesString".equals(name)) {
          continue;
        }
        if ("sources".equals(name) && oldArgs.containsKey("source")) {
          continue;
        }
        if ("sourcesString".equals(name)) {
          continue;
        }
        newArgs.put(name, arg.getValue());
      }
      return new UninstantiatedDescribable(ud.getSymbol(), ud.getKlass(), newArgs);
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.Restore_DisplayName();
    }

    @Override
    protected boolean isApplicableToFreeStyleProjects(@NonNull FreeStyleCommandConfiguration configuration) {
      return configuration.isRestoreAllowed();
    }

  }

  //endregion

}
