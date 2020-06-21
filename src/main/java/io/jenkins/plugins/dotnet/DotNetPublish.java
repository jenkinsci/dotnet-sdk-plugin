package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import hudson.util.VariableResolver;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A build step to run "{@code dotnet publish}", publishing a project. */
public class DotNetPublish extends DotNetUsingMSBuild {

  /** Creates a new "{@code dotnet publish}" build step. */
  @DataBoundConstructor
  public DotNetPublish() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet publish}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code publish}</li>
   *   <li>Any arguments added by {@link DotNetUsingMSBuild#addCommandLineArguments(Run, ArgumentListBuilder, VariableResolver, Set)}.</li>
   *   <li>{@code --force}, if requested via {@link #setForce(boolean)}.</li>
   *   <li>{@code -f:xxx}, if a target framework moniker has been specified via {@link #setFramework(String)}.</li>
   *   <li>{@code --manifest xxx} for each manifest specified via {@link #setManifests(String[])}.</li>
   *   <li>{@code --no-build}, if requested via {@link #setNoBuild(boolean)}.</li>
   *   <li>{@code --no-dependencies}, if requested via {@link #setNoDependencies(boolean)}.</li>
   *   <li>{@code --no-restore}, if requested via {@link #setNoRestore(boolean)}.</li>
   *   <li>{@code -r:xxx}, if a runtime identifier has been specified via {@link #setRuntime(String)}.</li>
   *   <li>{@code --self-contained true/false}, if a value has been specified via {@link #setSelfContained(Boolean)}.</li>
   *   <li>{@code --version-suffix xxx}, if a version suffix has been specified via {@link #setRuntime(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
    args.add("publish");
    super.addCommandLineArguments(run, args, resolver, sensitive);
    if (this.force)
      args.add("--force");
    if (this.framework != null)
      args.add("-f:" + this.framework);
    if (this.manifests != null) {
      for (final String manifest : this.manifests)
        args.add("--manifest", manifest);
    }
    if (this.noBuild)
      args.add("--no-build");
    if (this.noDependencies)
      args.add("--no-dependencies");
    if (this.noRestore)
      args.add("--no-restore");
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
    if (this.selfContained != null)
      args.add("--self-contained", this.selfContained ? "true" : "false");
    if (this.versionSuffix != null)
      args.add("--version-suffix", this.versionSuffix);
  }

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
   * @param force  {@code true} to resolve all dependencies even if the last restore was successful; {@code false} otherwise.
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

  private String[] manifests;

  /**
   * Gets the manifests to use.
   *
   * @return The manifests to use.
   */
  @CheckForNull
  public String[] getManifests() {
    if (this.manifests == null)
      return null;
    return this.manifests.clone();
  }

  /**
   * Sets the manifests to use.
   *
   * @param manifests The manifests to use.
   */
  @DataBoundSetter
  public void setManifests(@CheckForNull String[] manifests) {
    if (manifests != null) {
      final List<String> cleaned = new ArrayList<>();
      for (final String manifestLine : manifests) {
        if (manifestLine == null)
          continue;
        for (final String manifest : manifestLine.split("[\r\n]")) {
          final String clean = Util.fixEmptyAndTrim(manifest);
          if (clean != null)
            cleaned.add(clean);
        }
      }
      if (cleaned.isEmpty())
        this.manifests = null;
      else
        this.manifests = cleaned.toArray(new String[0]);
    }
    else
      this.manifests = null;
  }

  private boolean noBuild;

  /**
   * Determines whether or not a build should be performed before publishing.
   *
   * @return {@code true} when neither a restore nor a build will be performed before publishing; {@code false} otherwise.
   */
  public boolean isNoBuild() {
    return this.noBuild;
  }

  /**
   * Determines whether or not a build should be performed before publishing.
   *
   * @param noBuild {@code true} to perform neither a restore nor a build before publishing; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoBuild(boolean noBuild) {
    this.noBuild = noBuild;
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

  private Boolean selfContained;

  /**
   * Determines whether or not the project should be published self-contained.
   *
   * @return {@code null} when the project decides; {@code true} when publishing self-contained; {@code false} otherwise.
   */
  @CheckForNull
  public Boolean getSelfContained() {
    return this.selfContained;
  }

  /**
   * Determines whether or not the project should be published self-contained.
   *
   * @param selfContained {@code null} to let the project decide; {@code true} to publish self-contained; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setSelfContained(@CheckForNull Boolean selfContained) {
    this.selfContained = selfContained;
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

  /** A descriptor for "{@code dotnet publish}" build steps. */
  @Extension
  @Symbol("dotnetPublish")
  public static final class DescriptorImpl extends MSBuildCommandDescriptor {

    /** Creates a new "{@code dotnet publish}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Fills a listbox with the possible values for the "self-containing" setting.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillSelfContainedItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNetPublish_ProjectDefault(), null);
      model.add(Messages.DotNetPublish_Yes(), "true");
      model.add(Messages.DotNetPublish_No(), "false");
      return model;
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetPublish_DisplayName();
    }

  }

  //endregion

}
