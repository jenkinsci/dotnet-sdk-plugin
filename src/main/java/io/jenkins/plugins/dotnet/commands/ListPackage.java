package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.structs.describable.UninstantiatedDescribable;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.HashMap;
import java.util.Map;

/** A build step to run "{@code dotnet list package}", showing a project's package dependencies. */
public final class ListPackage extends Command {

  /** Creates a new "{@code dotnet list package}" build step. */
  @DataBoundConstructor
  public ListPackage() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet list package}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code list}</li>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>{@code package}</li>
   *   <li>{@code --deprecated}, if requested via {@link #setDeprecated(boolean)}.</li>
   *   <li>{@code --outdated}, if requested via {@link #setOutdated(boolean)}.</li>
   *   <li>
   *     {@code --framework xxx} for each target framework moniker specified via {@link #setFramework(String)},
   *     {@link #setFrameworks(String...)} or {@link #setFrameworksString(String)}.
   *   </li>
   *   <li>{@code --include-transitive}, if requested via {@link #setIncludeTransitive(boolean)}.</li>
   * </ol>
   * If either outdated or deprecated packages are requested, this also adds:
   * <ol>
   *   <li>{@code --include-prerelease}, if requested via {@link #setIncludePrerelease(boolean)}.</li>
   *   <li>{@code --highest-minor}, if requested via {@link #setHighestMinor(boolean)}.</li>
   *   <li>{@code --highest-patch}, if requested via {@link #setHighestPatch(boolean)}.</li>
   *   <li>{@code --config xxx}, if a configuration file was specified via {@link #setConfig(String)}.</li>
   *   <li>
   *     {@code --source xxx} for each source specified via {@link #setSource(String)}, {@link #setSources(String...)} or
   *     {@link #setSourcesString(String)}.
   *   </li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) {
    args.add("list");
    args.addOption(this.project);
    args.add("package");
    args.addFlag("deprecated", this.deprecated);
    args.addFlag("outdated", this.outdated);
    args.addOptions("framework", this.frameworks);
    args.addFlag("include-transitive", this.includeTransitive);
    if (this.outdated || this.deprecated) {
      args.addFlag("include-prerelease", this.includePrerelease);
      args.addFlag("highest-minor", this.highestMinor);
      args.addFlag("highest-patch", this.highestPatch);
      args.addOption("config", this.config);
      args.addOptions("source", this.sources);
    }
  }

  //region Properties

  private String config;

  /**
   * Gets the {@code NuGet.config} file to use.
   *
   * @return The {@code NuGet.config} file to use.
   */
  @CheckForNull
  public String getConfig() {
    return this.config;
  }

  /**
   * Sets the {@code NuGet.config} file to use.
   *
   * @param config The {@code NuGet.config} file to use.
   */
  @DataBoundSetter
  public void setConfig(@CheckForNull String config) {
    this.config = Util.fixEmptyAndTrim(config);
  }

  private boolean deprecated;

  /**
   * Determines whether or not deprecated packages should be shown.
   *
   * @return {@code true} if deprecated packages should be shown; {@code false} otherwise.
   */
  public boolean isDeprecated() {
    return this.deprecated;
  }

  /**
   * Determines whether or not deprecated packages should be shown.
   *
   * @param deprecated {@code true} to show deprecated packages; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }

  private String frameworks;

  /**
   * Gets the sole target framework moniker to use.
   *
   * @return The sole target framework moniker to use, or {@code null} when there is not exactly one target framework moniker set.
   */
  @CheckForNull
  public String getFramework() {
    if (this.frameworks == null)
      return null;
    final String[] frameworks = Util.tokenize(this.frameworks);
    if (frameworks.length != 1)
      return null;
    return frameworks[0];
  }

  /**
   * Sets the sole target framework moniker to use.
   * <p>
   * To set more than one, use {@link #setFrameworks(String...)} instead.
   *
   * @param framework The sole target framework moniker to use.
   */
  @DataBoundSetter
  public void setFramework(@CheckForNull String framework) {
    this.frameworks = Util.fixEmptyAndTrim(framework);
  }

  /**
   * Gets the target framework monikers to use.
   *
   * @return The target framework monikers to use.
   */
  @CheckForNull
  public String[] getFrameworks() {
    if (this.frameworks == null)
      return null;
    return Util.tokenize(this.frameworks);
  }

  /**
   * Sets the target framework monikers to use.
   *
   * @param frameworks The target framework monikers to use.
   */
  @DataBoundSetter
  public void setFrameworks(@CheckForNull String... frameworks) {
    this.frameworks = DotNetUtils.detokenize(frameworks, ' ');
  }

  /**
   * Gets the target framework monikers to use.
   *
   * @return The target framework monikers to use.
   */
  @CheckForNull
  public String getFrameworksString() {
    return this.frameworks;
  }

  /**
   * Sets the target framework monikers to use.
   *
   * @param frameworks The target framework monikers to use.
   */
  @DataBoundSetter
  public void setFrameworksString(@CheckForNull String frameworks) {
    this.frameworks = Util.fixEmptyAndTrim(frameworks);
  }

  private boolean highestMinor;

  /**
   * Determines whether or not to consider only packages where at most the minor version has changed.
   *
   * @return {@code true} if only packages where at most the minor version has changed are considered; {@code false} otherwise.
   */
  public boolean isHighestMinor() {
    return this.highestMinor;
  }

  /**
   * Determines whether or not to consider only packages where at most the minor version has changed.
   *
   * @param highestMinor {@code true} to only consider packages where at most the minor version has changed; {@code false}
   *                     otherwise.
   */
  @DataBoundSetter
  public void setHighestMinor(boolean highestMinor) {
    this.highestMinor = highestMinor;
  }

  private boolean highestPatch;

  /**
   * Determines whether or not to consider only packages where at most the patch version has changed.
   *
   * @return {@code true} if only packages where at most the patch version has changed are considered; {@code false} otherwise.
   */
  public boolean isHighestPatch() {
    return this.highestPatch;
  }

  /**
   * Determines whether or not to consider only packages where at most the patch version has changed.
   *
   * @param highestPatch {@code true} to only consider packages where at most the patch version has changed; {@code false}
   *                     otherwise.
   */
  @DataBoundSetter
  public void setHighestPatch(boolean highestPatch) {
    this.highestPatch = highestPatch;
  }

  private boolean includePrerelease;

  /**
   * Determines whether or not to consider prerelease packages.
   *
   * @return {@code true} if prerelease packages are considered; {@code false} otherwise.
   */
  public boolean isIncludePrerelease() {
    return this.includePrerelease;
  }

  /**
   * Determines whether or not to consider prerelease packages.
   *
   * @param includePrerelease {@code true} to consider prerelease packages; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIncludePrerelease(boolean includePrerelease) {
    this.includePrerelease = includePrerelease;
  }

  private boolean includeTransitive;

  /**
   * Determines whether or not to show transitive dependencies.
   *
   * @return {@code true} if transitive dependencies are shown; {@code false} otherwise.
   */
  public boolean isIncludeTransitive() {
    return this.includeTransitive;
  }

  /**
   * Determines whether or not to show transitive dependencies.
   *
   * @param includeTransitive {@code true} to show transitive dependencies; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIncludeTransitive(boolean includeTransitive) {
    this.includeTransitive = includeTransitive;
  }

  private boolean outdated;

  /**
   * Determines whether or not outdated packages should be shown.
   *
   * @return {@code true} if outdated packages should be shown; {@code false} otherwise.
   */
  public boolean isOutdated() {
    return this.outdated;
  }

  /**
   * Determines whether or not outdated packages should be shown.
   *
   * @param outdated {@code true} to show outdated packages; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setOutdated(boolean outdated) {
    this.outdated = outdated;
  }

  private String project;

  /**
   * Gets the project to list packages for.
   *
   * @return The project to list packages for.
   */
  @CheckForNull
  public String getProject() {
    return this.project;
  }

  /**
   * Sets the project to list packages for.
   *
   * @param project The project to list packages for.
   */
  @DataBoundSetter
  public void setProject(@CheckForNull String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  private String sources;

  /**
   * Gets the sole package source to use.
   *
   * @return The sole package source to use, or {@code null} when there is not exactly one package source set.
   */
  @CheckForNull
  public String getSource() {
    if (this.sources == null)
      return null;
    final String[] sources = Util.tokenize(this.sources);
    if (sources.length != 1)
      return null;
    return sources[0];
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
    this.sources = Util.fixEmptyAndTrim(source);
  }

  /**
   * Gets the package sources to use.
   *
   * @return The package sources to use.
   */
  @CheckForNull
  public String[] getSources() {
    if (this.sources == null)
      return null;
    return Util.tokenize(this.sources);
  }

  /**
   * Sets the package sources to use.
   *
   * @param sources The package sources to use.
   */
  @DataBoundSetter
  public void setSources(@CheckForNull String... sources) {
    this.sources = DotNetUtils.detokenize(sources, ' ');
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

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet list package}" build steps. */
  @Extension
  @Symbol("dotnetListPackage")
  public static final class DescriptorImpl extends CommandDescriptor {

    /** Creates a new "{@code dotnet list package}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Performs validation on the "config file" setting.
     *
     * @param value      The specified configuration file name.
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckConfig(@CheckForNull @QueryParameter String value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (Util.fixEmptyAndTrim(value) != null && !deprecated && !outdated)
        return FormValidation.warning(Messages.ListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    /**
     * Performs validation on the "show deprecated packages" setting.
     *
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckDeprecated(@QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (deprecated && outdated)
        return FormValidation.error(Messages.ListPackage_EitherDeprecatedOrOutdated());
      return FormValidation.ok();
    }

    /**
     * Performs validation on the "highest minor" setting.
     *
     * @param value      Flag indicating whether or not the minor version is the highest version that is allowed to change.
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckHighestMinor(@QueryParameter boolean value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (value && !deprecated && !outdated)
        return FormValidation.warning(Messages.ListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    /**
     * Performs validation on the "highest patch" setting.
     *
     * @param value      Flag indicating whether or not the patch version is the highest version that is allowed to change.
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckHighestPatch(@QueryParameter boolean value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (value && !deprecated && !outdated)
        return FormValidation.warning(Messages.ListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    /**
     * Performs validation on the "include prerelease" setting.
     *
     * @param value      Flag indicating whether or not prerelease package versions should be listed.
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckIncludePrerelease(@QueryParameter boolean value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (value && !deprecated && !outdated)
        return FormValidation.warning(Messages.ListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    /**
     * Performs validation on the "show outdated packages" setting.
     *
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckOutdated(@QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (deprecated && outdated)
        return FormValidation.error(Messages.ListPackage_EitherDeprecatedOrOutdated());
      return FormValidation.ok();
    }

    /**
     * Performs validation on the "sources" setting.
     *
     * @param value      The specified package sources.
     * @param deprecated Flag indicating whether or not deprecated packages should be listed.
     * @param outdated   Flag indicating whether or not outdated packages should be listed.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckSources(@CheckForNull @QueryParameter String value, @QueryParameter boolean deprecated, @QueryParameter boolean outdated) {
      if (Util.fixEmptyAndTrim(value) != null && !deprecated && !outdated)
        return FormValidation.warning(Messages.ListPackage_OnlyForPackageUpdateSearch());
      return FormValidation.ok();
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.ListPackage_DisplayName();
    }

    @NonNull
    @Override
    public UninstantiatedDescribable customUninstantiate(@NonNull UninstantiatedDescribable ud) {
      ud = super.customUninstantiate(ud);
      final Map<String, ?> oldArgs = ud.getArguments();
      final Map<String, Object> newArgs = new HashMap<>();
      for (final Map.Entry<String, ?> arg : oldArgs.entrySet()) {
        final String name = arg.getKey();
        if ("frameworks".equals(name) && oldArgs.containsKey("framework"))
          continue;
        if ("frameworksString".equals(name))
          continue;
        if ("sources".equals(name) && oldArgs.containsKey("source"))
          continue;
        if ("sourcesString".equals(name))
          continue;
        newArgs.put(name, arg.getValue());
      }
      return new UninstantiatedDescribable(ud.getSymbol(), ud.getKlass(), newArgs);
    }

  }

  //endregion

}
