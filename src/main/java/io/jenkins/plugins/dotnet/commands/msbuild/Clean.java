package io.jenkins.plugins.dotnet.commands.msbuild;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/** A build step to run "{@code dotnet clean}", cleaning up a project's build artifacts. */
public final class Clean extends MSBuildCommand {

  /** Creates a new "{@code dotnet clean}" build step. */
  @DataBoundConstructor
  public Clean() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet clean}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code clean}</li>
   *   <li>Any arguments added by {@link MSBuildCommand#addCommandLineArguments(Run, ArgumentListBuilder, VariableResolver, Set)}.</li>
   *   <li>{@code -f:xxx}, if a target framework moniker has been specified via {@link #setFramework(String)}.</li>
   *   <li>{@code -r:xxx}, if a runtime identifier has been specified via {@link #setRuntime(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
    args.add("clean");
    super.addCommandLineArguments(run, args, resolver, sensitive);
    if (this.framework != null)
      args.add("-f:" + this.framework);
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
  }

  //region Properties

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
   * Gets the runtime identifier to use.
   *
   * @param runtime The runtime identifier to use.
   */
  @DataBoundSetter
  public void setRuntime(@CheckForNull String runtime) {
    this.runtime = Util.fixEmptyAndTrim(runtime);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet clean}" build steps. */
  @Extension
  @Symbol("dotnetClean")
  public static final class DescriptorImpl extends MSBuildCommandDescriptor {

    /** Creates a new "{@code dotnet clean}" build step descriptor instance. */
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
      return Messages.MSBuild_Clean_DisplayName();
    }

  }

  //endregion

}
