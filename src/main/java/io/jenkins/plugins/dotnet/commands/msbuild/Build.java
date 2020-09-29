package io.jenkins.plugins.dotnet.commands.msbuild;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** A build step to run "{@code dotnet build}", building a project. */
public final class Build extends MSBuildCommand {

  /** Creates a new "{@code dotnet build}" build step. */
  @DataBoundConstructor
  public Build() {
    super("build");
  }

  /**
   * Adds command line arguments for this "{@code dotnet build}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link MSBuildCommand#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>{@code --force}, if requested via {@link #setForce(boolean)}.</li>
   *   <li>{@code --no-dependencies}, if requested via {@link #setNoDependencies(boolean)}.</li>
   *   <li>{@code --no-incremental}, if requested via {@link #setNoIncremental(boolean)}.</li>
   *   <li>{@code --no-restore}, if requested via {@link #setNoRestore(boolean)}.</li>
   *   <li>{@code -f:xxx}, if a target framework moniker has been specified via {@link #setFramework(String)}.</li>
   *   <li>{@code -r:xxx}, if a runtime identifier has been specified via {@link #setRuntime(String)}.</li>
   *   <li>{@code -t:xxx} for each target specified via {@link #setTargetString(String)}.</li>
   *   <li>{@code --version-suffix xxx}, if a version suffix has been specified via {@link #setRuntime(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) {
    super.addCommandLineArguments(args);
    args.addFlag("force", this.force);
    args.addFlag("no-dependencies", this.noDependencies);
    args.addFlag("no-incremental", this.noIncremental);
    args.addFlag("no-restore", this.noRestore);
    args.addOption('f', this.framework);
    args.addOption('r', this.runtime);
    args.addOptions('t', this.targets, Build.TARGETS_DELIMITER);
    args.addOption("version-suffix", this.versionSuffix);
  }

  private static final String TARGETS_DELIMITER = "; \t\n\r\f";

  //region Properties

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

  private String framework;

  /**
   * Gets the target framework moniker to use.
   *
   * @return The target framework moniker to use.
   */
  @CheckForNull
  public String getFramework() {
    return this.framework;
  }

  /**
   * Sets the target framework moniker to use.
   *
   * @param framework The target framework moniker to use.
   */
  @DataBoundSetter
  public void setFramework(@CheckForNull String framework) {
    this.framework = Util.fixEmptyAndTrim(framework);
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

  private boolean noIncremental;

  /**
   * Determines whether or not incremental builds are allowed.
   *
   * @return {@code true} when incremental builds are disabled; {@code false} otherwise.
   */
  public boolean isNoIncremental() {
    return this.noIncremental;
  }

  /**
   * Determines whether or not incremental builds are allowed.
   *
   * @param noIncremental {@code true} to disallow incremental builds; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoIncremental(boolean noIncremental) {
    this.noIncremental = noIncremental;
  }

  private boolean noRestore;

  /**
   * Determines whether or not an implicit restore should be executed as part of this command.
   *
   * @return {@code true} when the implicit restore is disabled; {@code false} otherwise.
   */
  public boolean isNoRestore() {
    return this.noRestore;
  }

  /**
   * Determines whether or not an implicit restore should be executed as part of this command.
   *
   * @param noRestore {@code true} to disable the implicit restore; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoRestore(boolean noRestore) {
    this.noRestore = noRestore;
  }

  private String runtime;

  /**
   * Gets the runtime identifier to use.
   *
   * @return The runtime identifier to use.
   */
  @CheckForNull
  public String getRuntime() {
    return this.runtime;
  }

  /**
   * Sets the runtime identifier to use.
   *
   * @param runtime The runtime identifier to use.
   */
  @DataBoundSetter
  public void setRuntime(@CheckForNull String runtime) {
    this.runtime = Util.fixEmptyAndTrim(runtime);
  }

  private String targets;

  /**
   * Gets the targets to build.
   *
   * @return The targets to build.
   *
   * @deprecated Use {@link #getTargets()} instead.
   */
  @Deprecated
  @CheckForNull
  public String getTargetString() {
    return this.targets;
  }

  /**
   * Sets the targets to build.
   *
   * @param targets The targets to build.
   *
   * @deprecated Use {@link #setTargets(String...)} instead.
   */
  @Deprecated
  @DataBoundSetter
  public void setTargetString(@CheckForNull String targets) {
    this.targets = Util.fixEmptyAndTrim(targets);
  }

  /**
   * Gets the targets to build.
   *
   * @return The targets to build.
   */
  @CheckForNull
  public String[] getTargets() {
    if (this.targets == null)
      return null;
    return Util.tokenize(this.targets, Build.TARGETS_DELIMITER);
  }

  /**
   * Sets the targets to build.
   *
   * @param targets The targets to build.
   */
  @DataBoundSetter
  public void setTargets(@CheckForNull String... targets) {
    this.targets = DotNetUtils.detokenize(targets, ';');
  }

  private String versionSuffix;

  /**
   * Sets the version suffix to use.
   *
   * @return The version suffix to use.
   */
  @CheckForNull
  public String getVersionSuffix() {
    return this.versionSuffix;
  }

  /**
   * Sets the version suffix to use.
   *
   * @param versionSuffix The version suffix to use.
   */
  @DataBoundSetter
  public void setVersionSuffix(@CheckForNull String versionSuffix) {
    this.versionSuffix = Util.fixEmptyAndTrim(versionSuffix);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet build}" build steps. */
  @Extension
  @Symbol("dotnetBuild")
  public static final class DescriptorImpl extends MSBuildCommandDescriptor {

    /** Creates a new "{@code dotnet build}" build step descriptor instance. */
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
      return Messages.MSBuild_Build_DisplayName();
    }

  }

  //endregion

}
