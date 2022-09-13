package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.jenkins.plugins.dotnet.extensions.commands.Command;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;

public final class DotNetBuilder extends Builder {

  /**
   * Creates a new .NET builder.
   *
   * @param command The command to run.
   */
  @DataBoundConstructor
  public DotNetBuilder(@NonNull Command command) {
    this.command = command;
  }

  //region Properties

  /** The command to run. */
  @NonNull
  private final Command command;

  /** The settings for the step execution. */
  @NonNull
  private final DotNetStepExecution.Settings settings = new DotNetStepExecution.Settings();

  /**
   * Gets the specific charset to use for the command's output.
   *
   * @return The specific charset to use for the command's output, or {@code null} to indicate that the build's default charset
   * should be used.
   */
  @CheckForNull
  public String getCharset() {
    return this.settings.charset;
  }

  /**
   * Sets the specific charset to use for the command's output.
   *
   * @param charset The specific charset to use for the command's output, or {@code null} to indicate that the build's default
   *                charset should be used.
   */
  @DataBoundSetter
  public void setCharset(@CheckForNull String charset) {
    this.settings.charset = Util.fixEmptyAndTrim(charset);
  }

  /**
   * Gets the command to be run by this step.
   *
   * @return The command to run.
   */
  @NonNull
  public Command getCommand() {
    return this.command;
  }

  /**
   * Determines whether the build should continue when there is an error.
   *
   * @return {@code false} if the build should be aborted when there is an error; {@code true} to set build status to failed or
   * unstable, based on configuration, but allow the next build step to execute.
   */
  @SuppressWarnings("unused")
  public boolean isContinueOnError() {
    return this.settings.continueOnError;
  }

  /**
   * Determines whether the build should continue when there is an error.
   *
   * @param continueOnError {@code false} if the build should be aborted when there is an error; {@code true} if the build status
   *                        should be set to failed or unstable, based on configuration, allowing the next build step to execute.
   */
  @DataBoundSetter
  public void setContinueOnError(boolean continueOnError) {
    this.settings.continueOnError = continueOnError;
  }

  /**
   * Gets the name of the SDK to use.
   *
   * @return The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @CheckForNull
  public String getSdk() {
    return this.settings.sdk;
  }

  /**
   * Sets the name of the SDK to use.
   *
   * @param sdk The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @DataBoundSetter
  public void setSdk(@CheckForNull String sdk) {
    this.settings.sdk = Util.fixEmpty(sdk);
  }

  /**
   * Determines whether a specific SDK version should be used.
   *
   * @return {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK (as opposed to a
   * more recent one that happens to be installed on the build agent); {@code false} otherwise.
   */
  @SuppressWarnings("unused")
  public boolean isSpecificSdkVersion() {
    return this.settings.specificSdkVersion;
  }

  /**
   * Determines whether a specific SDK version should be used.
   *
   * @param specificSdkVersion {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK
   *                           (as opposed to a more recent one that happens to be installed on the build agent); {@code false}
   *                           otherwise.
   */
  @DataBoundSetter
  public void setSpecificSdkVersion(boolean specificSdkVersion) {
    this.settings.specificSdkVersion = specificSdkVersion;
  }

  /**
   * Determines whether the presence of errors makes the build unstable (instead of failed).
   *
   * @return {@code true} if errors cause the build to be marked as unstable (instead of failed); {@code false} otherwise.
   */
  public boolean isUnstableIfErrors() {
    return this.settings.unstableIfErrors;
  }

  /**
   * Determines whether the presence of errors makes the build unstable (instead of failed).
   *
   * @param unstableIfErrors {@code true} if errors cause the build to be marked as unstable (instead of failed); {@code false}
   *                         otherwise.
   */
  @DataBoundSetter
  public void setUnstableIfErrors(boolean unstableIfErrors) {
    this.settings.unstableIfErrors = unstableIfErrors;
  }

  /**
   * Determines whether the presence of warnings makes the build unstable (instead of successful).
   *
   * @return {@code true} if warnings cause the build to be marked as unstable (instead of successful); {@code false} otherwise.
   */
  public boolean isUnstableIfWarnings() {
    return this.settings.unstableIfWarnings;
  }

  /**
   * Determines whether the presence of warnings makes the build unstable (instead of successful).
   *
   * @param unstableIfWarnings {@code true} if warnings cause the build to be marked as unstable (instead of successful);
   *                           {@code false} otherwise.
   */
  @DataBoundSetter
  public void setUnstableIfWarnings(boolean unstableIfWarnings) {
    this.settings.unstableIfWarnings = unstableIfWarnings;
  }

  /**
   * Gets the working directory to use for the command.
   *
   * @return The working directory to use for the command.
   */
  @CheckForNull
  @SuppressWarnings("unused")
  public String getWorkDirectory() {
    return this.settings.workDirectory;
  }

  /**
   * Sets the working directory to use for the command.
   *
   * @param workDirectory The working directory to use for the command.
   */
  @DataBoundSetter
  public void setWorkDirectory(@CheckForNull String workDirectory) {
    this.settings.workDirectory = Util.fixEmpty(workDirectory);
  }

  //endregion

  @Override
  public boolean perform(@NonNull AbstractBuild<?, ?> build, @NonNull Launcher launcher,
                         @NonNull BuildListener listener) throws InterruptedException, IOException {
    final EnvVars env = build.getEnvironment(listener);
    final FilePath workspace = build.getWorkspace();
    if (workspace == null) {
      throw new AbortException(Messages.DotNetBuilder_NoWorkspace(build));
    }
    DotNetStepExecution.perform(this.command, this.settings, build, workspace, env, launcher, listener);
    return true;
  }

  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Builder> implements DotNetBuildStepDescriptor {

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetBuilder_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

  }

}
