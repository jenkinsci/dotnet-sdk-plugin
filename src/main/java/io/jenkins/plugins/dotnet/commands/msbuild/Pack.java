package io.jenkins.plugins.dotnet.commands.msbuild;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.FreeStyleCommandConfiguration;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** A build step to run "{@code dotnet pack}", creating a NuGet package for a project. */
public final class Pack extends MSBuildCommand {

  /** Creates a new "{@code dotnet pack}" build step. */
  @DataBoundConstructor
  public Pack() {
    super("pack");
  }

  /**
   * Adds command line arguments for this "{@code dotnet pack}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link MSBuildCommand#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>{@code --force}, if requested via {@link #setForce(boolean)}.</li>
   *   <li>{@code --include-source}, if requested via {@link #setIncludeSource(boolean)}.</li>
   *   <li>{@code --include-symbols}, if requested via {@link #setIncludeSymbols(boolean)}.</li>
   *   <li>{@code --no-build}, if requested via {@link #setNoBuild(boolean)}.</li>
   *   <li>{@code --no-dependencies}, if requested via {@link #setNoDependencies(boolean)}.</li>
   *   <li>{@code --no-restore}, if requested via {@link #setNoRestore(boolean)}.</li>
   *   <li>{@code -r:xxx}, if a runtime identifier has been specified via {@link #setRuntime(String)}.</li>
   *   <li>{@code --version-suffix xxx}, if a version suffix has been specified via {@link #setRuntime(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) {
    super.addCommandLineArguments(args);
    args.addFlag("force", this.force);
    args.addFlag("include-source", this.includeSource);
    args.addFlag("include-symbols", this.includeSymbols);
    args.addFlag("no-build", this.noBuild);
    args.addFlag("no-dependencies", this.noDependencies);
    args.addFlag("no-restore", this.noRestore);
    args.addOption('r', this.runtime);
    args.addOption("version-suffix", this.versionSuffix);
  }

  //region Properties

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

  private boolean includeSource;

  /**
   * Determines whether symbol packages containing source code should be created.
   *
   * @return {@code true} when symbol packages are created containing source code; {@code false} otherwise.
   */
  public boolean isIncludeSource() {
    return this.includeSource;
  }

  /**
   * Determines whether symbol packages containing source code should be created.
   *
   * @param includeSource {@code true} to create symbol packages containing source code; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIncludeSource(boolean includeSource) {
    this.includeSource = includeSource;
  }

  private boolean includeSymbols;

  /**
   * Determines whether symbol packages should be created.
   *
   * @return {@code true} when symbol packages are created; {@code false} otherwise.
   */
  public boolean isIncludeSymbols() {
    return this.includeSymbols;
  }

  /**
   * Determines whether symbol packages should be created.
   *
   * @param includeSymbols {@code true} to create symbol packages; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIncludeSymbols(boolean includeSymbols) {
    this.includeSymbols = includeSymbols;
  }

  private boolean noBuild;

  /**
   * Determines whether a build should be performed before creating the packages.
   *
   * @return {@code true} when neither a restore nor a build will be performed before creating the packages; {@code false}
   * otherwise.
   */
  public boolean isNoBuild() {
    return this.noBuild;
  }

  /**
   * Determines whether a build should be performed before creating the packages.
   *
   * @param noBuild {@code true} to perform neither a restore nor a build before creating the packages; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoBuild(boolean noBuild) {
    this.noBuild = noBuild;
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

  private boolean noRestore;

  /**
   * Determines whether an implicit restore should be executed as part of this command.
   *
   * @return {@code true} when the implicit restore is disabled; {@code false} otherwise.
   */
  public boolean isNoRestore() {
    return this.noRestore;
  }

  /**
   * Determines whether an implicit restore should be executed as part of this command.
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

  /** A descriptor for "{@code dotnet pack}" build steps. */
  @Extension
  @Symbol("dotnetPack")
  public static final class DescriptorImpl extends MSBuildCommandDescriptor {

    /** Creates a new "{@code dotnet pack}" build step descriptor instance. */
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
      return Messages.MSBuild_Pack_DisplayName();
    }

    @Override
    protected boolean isApplicableToFreeStyleProjects(@NonNull FreeStyleCommandConfiguration configuration) {
      return configuration.isPackAllowed();
    }

  }

  //endregion

}
