package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import io.jenkins.plugins.dotnet.DotNetSDK;
import io.jenkins.plugins.dotnet.DotNetStepExecution;
import io.jenkins.plugins.dotnet.extensions.commands.CommandLineArgumentProvider;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;

/** A build step executing a .NET CLI command. */
public class Command extends Builder implements SimpleBuildStep {

  /** {@inheritDoc} */
  @Override
  public CommandDescriptor getDescriptor() {
    return (CommandDescriptor) super.getDescriptor();
  }

  /**
   * Adds command line arguments for this invocation of the {@code dotnet} CLI.
   *
   * @param args The current set of arguments.
   *
   * @throws AbortException When something goes wrong.
   */
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    // nothing to add at this level
  }

  /**
   * Gets the descriptor for a .NET SDK.
   *
   * @return The descriptor for a .NET SDK.
   *
   * @throws AbortException When the descriptor could not be found.
   */
  @NonNull
  public static DotNetSDK.DescriptorImpl getSdkDescriptor() throws AbortException {
    // spotbugs does not like explicit throws of NullPointerException (see https://github.com/spotbugs/spotbugs/issues/1175), so we
    // need to do the test explicitly instead of using Objects.requireNonNull.
    final DotNetSDK.DescriptorImpl sdkDescriptor = ToolInstallation.all().get(DotNetSDK.DescriptorImpl.class);
    if (sdkDescriptor == null) {
      throw new AbortException(".NET SDK descriptor not found.");
    }
    return sdkDescriptor;
  }

  private final class CompatibilityLayer implements CommandLineArgumentProvider {

    @Override
    public void addCommandLineArguments(@NonNull io.jenkins.plugins.dotnet.DotNetArguments arguments) throws AbortException {
      final DotNetArguments oldArguments = new DotNetArguments(arguments);
      Command.this.addCommandLineArguments(oldArguments);
    }

  }

  /**
   * Runs this .NET command.
   *
   * @param run       The run context for the command.
   * @param workspace The base working directory for the command.
   * @param env       The environment variables that apply for the command.
   * @param launcher  The launcher to use to execute the command.
   * @param listener  The listener to report command output to.
   *
   * @throws InterruptedException When execution is interrupted.
   * @throws IOException          When an I/O error occurs.
   */
  @Override
  public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher,
                      @NonNull TaskListener listener) throws InterruptedException, IOException {
    final DotNetStepExecution.Settings settings = new DotNetStepExecution.Settings();
    settings.charset = this.charset;
    settings.continueOnError = this.continueOnError;
    settings.sdk = this.sdk;
    settings.showSdkInfo = this.showSdkInfo;
    settings.shutDownBuildServers = this.shutDownBuildServers;
    settings.specificSdkVersion = this.specificSdkVersion;
    settings.unstableIfErrors = this.unstableIfErrors;
    settings.unstableIfWarnings = this.unstableIfWarnings;
    settings.workDirectory = this.workDirectory;
    DotNetStepExecution.perform(new CompatibilityLayer(), settings, run, workspace, env, launcher, listener);
  }

  //region Properties

  /** A specific charset to use for the command's output. If {@code null}, the build's default charset will be used. */
  @CheckForNull
  private String charset = null;

  /**
   * Gets the specific charset to use for the command's output.
   *
   * @return The specific charset to use for the command's output, or {@code null} to indicate that the build's default charset
   * should be used.
   */
  @CheckForNull
  public String getCharset() {
    return this.charset;
  }

  /**
   * Sets the specific charset to use for the command's output.
   *
   * @param charset The specific charset to use for the command's output, or {@code null} to indicate that the build's default
   *                charset should be used.
   */
  @DataBoundSetter
  public void setCharset(@CheckForNull String charset) {
    this.charset = Util.fixEmptyAndTrim(charset);
  }

  private boolean continueOnError = false;

  /**
   * Determines whether the build should continue when there is an error.
   *
   * @return {@code false} if the build should be aborted when there is an error; {@code true} to set build status to failed or
   * unstable, based on configuration, but allow the next build step to execute.
   */
  @SuppressWarnings("unused")
  public boolean isContinueOnError() {
    return this.continueOnError;
  }

  /**
   * Determines whether the build should continue when there is an error.
   *
   * @param continueOnError {@code false} if the build should be aborted when there is an error; {@code true} if the build status
   *                        should be set to failed or unstable, based on configuration, allowing the next build step to execute.
   */
  @DataBoundSetter
  public void setContinueOnError(boolean continueOnError) {
    this.continueOnError = continueOnError;
  }

  /** The name of the SDK to use. */
  @CheckForNull
  protected String sdk;

  /**
   * Gets the name of the SDK to use.
   *
   * @return The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @CheckForNull
  public String getSdk() {
    return this.sdk;
  }

  /**
   * Sets the name of the SDK to use.
   *
   * @param sdk The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @DataBoundSetter
  public void setSdk(@CheckForNull String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  /** Flag indicating whether SDK information should be shown. */
  protected boolean showSdkInfo = false;

  /**
   * Determines whether SDK information should be shown.
   *
   * @return {@code true} if "{@code dotnet --info}" should be run before the main command; {@code false} otherwise.
   */
  @SuppressWarnings("unused")
  public boolean isShowSdkInfo() {
    return this.showSdkInfo;
  }

  /**
   * Determines whether SDK information should be shown.
   *
   * @param showSdkInfo {@code true} if "{@code dotnet --info}" should be run before the main command; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setShowSdkInfo(boolean showSdkInfo) {
    this.showSdkInfo = showSdkInfo;
  }

  /** Flag indicating whether any build servers started by the main command should be shut down. */
  protected boolean shutDownBuildServers = false;

  /** Flag indicating whether a specific SDK version should be used. */
  private boolean specificSdkVersion = false;

  /**
   * Determines whether a specific SDK version should be used.
   *
   * @return {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK (as opposed to a
   * more recent one that happens to be installed on the build agent); {@code false} otherwise.
   */
  @SuppressWarnings("unused")
  public boolean isSpecificSdkVersion() {
    return this.specificSdkVersion;
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
    this.specificSdkVersion = specificSdkVersion;
  }

  /** Flag indicating whether the presence of errors makes the build unstable (instead of failed). */
  protected boolean unstableIfErrors = false;

  /** Flag indicating whether the presence of warnings makes the build unstable (instead of successful). */
  protected boolean unstableIfWarnings = false;

  /** The working directory to use for the command. This directory is <em>not</em> created by the command execution. */
  @CheckForNull
  protected String workDirectory = null;

  /**
   * Gets the working directory to use for the command.
   *
   * @return The working directory to use for the command.
   */
  @CheckForNull
  @SuppressWarnings("unused")
  public String getWorkDirectory() {
    return this.workDirectory;
  }

  /**
   * Sets the working directory to use for the command.
   *
   * @param workDirectory The working directory to use for the command.
   */
  @DataBoundSetter
  public void setWorkDirectory(@CheckForNull String workDirectory) {
    this.workDirectory = Util.fixEmpty(workDirectory);
  }

  //endregion

}
