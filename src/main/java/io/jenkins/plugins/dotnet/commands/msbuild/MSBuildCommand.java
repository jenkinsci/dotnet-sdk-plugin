package io.jenkins.plugins.dotnet.commands.msbuild;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.Command;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A build step executing an MSBuild-based .NET CLI command. */
public class MSBuildCommand extends Command {

  public MSBuildCommand() {
    this.command = null;
  }

  public MSBuildCommand(@NonNull String command) {
    this.command = command;
  }

  private final String command;

  /**
   * {@inheritDoc}
   * <p>
   * This adds:
   * <ol>
   *   <li>The command name, if applicable.</li>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>
   *     Any raw options specified via {@link #setOption(String)}, {@link #setOptions(String...)} or
   *     {@link #setOptionsString(String)}.
   *   </li>
   *   <li>{@code -c:xxx}, if a configuration has been specified via {@link #setConfiguration(String)}.</li>
   *   <li>{@code --nologo}, if requested via {@link #setNologo(boolean)}.</li>
   *   <li>{@code --output xxx}, if an output directory has been specified via {@link #setOutputDirectory(String)}.</li>
   *   <li>
   *     {@code -p:name=value}, for all properties specified via {@link #setProperties(Map)} or
   *     {@link #setPropertiesString(String)}.
   *   </li>
   *   <li>{@code -v:xxx}, if a verbosity has been specified via {@link #setVerbosity(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) {
    args.addOption(this.command);
    args.addOption(this.project);
    args.addOptions(this.options);
    args.addOption('c', this.configuration);
    args.addFlag("nologo", this.nologo);
    args.addOption("output", this.outputDirectory);
    try {
      args.addPropertyOptions("-p:", this.properties);
    }
    catch (IOException e) {
      MSBuildCommand.LOGGER.log(Level.FINE, Messages.MSBuild_Command_BadProperties(), e);
    }
    args.addOption('v', this.verbosity);
  }

  private static final Logger LOGGER = Logger.getLogger(MSBuildCommand.class.getName());

  //region Properties

  /** The project configuration to use. */
  @CheckForNull
  protected String configuration;

  /**
   * Gets the project configuration to use.
   *
   * @return The project configuration to use.
   */
  @CheckForNull
  public String getConfiguration() {
    return this.configuration;
  }

  /**
   * Sets the project configuration to use.
   *
   * @param configuration The project configuration to use.
   */
  @DataBoundSetter
  public void setConfiguration(@CheckForNull String configuration) {
    this.configuration = Util.fixEmptyAndTrim(configuration);
  }

  /** Flag indicating whether or not the MSBuild version/copyright lines should be suppressed. */
  protected boolean nologo;

  /**
   * Determines whether or not the MSBuild version/copyright lines should be suppressed.
   *
   * @return {@code true} if the MSBuild startup banner should be suppressed; {@code false} otherwise.
   */
  public boolean isNologo() {
    return this.nologo;
  }

  /**
   * Determines whether or not the MSBuild version/copyright lines should be suppressed.
   *
   * @param noLogo {@code true} if the MSBuild startup banner should be suppressed; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNologo(boolean noLogo) {
    this.nologo = noLogo;
  }

  /**
   * Additional options to pass to the command.
   * Options specified via more specific settings will take precedence over options specified here.
   */
  @CheckForNull
  protected String options;

  /**
   * Gets the single additional option to pass to the command.
   *
   * @return An additional option to pass to the command, or {@code null} when there is not exactly one such option set.
   */
  @CheckForNull
  public String getOption() {
    return DotNetUtils.singleToken(this.options);
  }

  /**
   * Sets a single additional option to pass to the command.
   * <p>
   * To set more than one, use {@link #setOptions(String...)} instead.
   *
   * @param option An additional option to pass to the command.
   */
  @DataBoundSetter
  public void setOption(@CheckForNull String option) {
    this.options = DotNetUtils.detokenize(' ', option);
  }

  /**
   * Gets additional options to pass to the command.
   *
   * @return Additional options to pass to the command.
   */
  @CheckForNull
  public String[] getOptions() {
    return DotNetUtils.tokenize(this.options);
  }

  /**
   * Sets additional options to pass to the command.
   * Options specified via more specific settings will take precedence over options specified here.
   *
   * @param options Additional options to pass to the command.
   */
  @DataBoundSetter
  public void setOptions(@CheckForNull String... options) {
    this.options = DotNetUtils.detokenize(' ', options);
  }

  /**
   * Gets additional options to pass to the command.
   * Options specified via more specific settings will take precedence over options specified here.
   *
   * @return Additional options to pass to the command.
   */
  @CheckForNull
  public String getOptionsString() {
    return this.options;
  }

  /**
   * Sets additional options to pass to the command.
   * Options specified via more specific settings will take precedence over options specified here.
   *
   * @param options Additional options to pass to the command.
   */
  @DataBoundSetter
  public void setOptionsString(@CheckForNull String options) {
    this.options = Util.fixEmptyAndTrim(options);
  }

  /** The output directory for the command. */
  @CheckForNull
  protected String outputDirectory;

  /**
   * Gets the output directory for the command.
   *
   * @return The output directory for the command.
   */
  @CheckForNull
  public String getOutputDirectory() {
    return this.outputDirectory;
  }

  /**
   * Sets the output directory for the command.
   *
   * @param outputDirectory The output directory for the command.
   */
  @DataBoundSetter
  public void setOutputDirectory(@CheckForNull String outputDirectory) {
    this.outputDirectory = Util.fixEmptyAndTrim(outputDirectory);
  }

  /** The project to process; for some commands this can also be a directory or a solution. */
  @CheckForNull
  protected String project;

  /**
   * Gets the name of the project file to process.
   * For some commands, this can also be a directory or a solution file.
   * When {@code null}, a project or solution from the current directory will usually be processed.
   *
   * @return The project to process.
   */
  @CheckForNull
  public String getProject() {
    return this.project;
  }

  /**
   * Sets the name of the project file to process.
   * For some commands, this can also be a directory or a solution file.
   * When {@code null}, a project or solution from the current directory will usually be processed.
   *
   * @param project The project to process.
   */
  @DataBoundSetter
  public void setProject(@CheckForNull String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  /** MSBuild properties to set. */
  @CheckForNull
  protected String properties;

  /**
   * Gets MSBuild properties to be applied to the command.
   *
   * @return MSBuild properties to be applied to the command (one key=value setting per line).
   */
  @CheckForNull
  public Map<String, String> getProperties() throws IOException {
    return DotNetUtils.createPropertyMap(this.properties);
  }

  /**
   * Sets MSBuild properties to be applied to the command.
   *
   * @param properties MSBuild properties to be applied to the command (one key=value setting per line).
   */
  @DataBoundSetter
  public void setProperties(@CheckForNull Map<String, String> properties) throws IOException {
    this.properties = DotNetUtils.createPropertyString(properties);
  }

  /**
   * Gets MSBuild properties to be applied to the command.
   *
   * @return MSBuild properties to be applied to the command (one key=value setting per line).
   */
  @CheckForNull
  public String getPropertiesString() {
    return this.properties;
  }

  /**
   * Sets MSBuild properties to be applied to the command.
   *
   * @param properties MSBuild properties to be applied to the command (one key=value setting per line).
   */
  @DataBoundSetter
  public void setPropertiesString(@CheckForNull String properties) {
    this.properties = Util.fixEmpty(properties);
  }

  /**
   * Determines whether or not any build servers started by the main command should be shut down.
   *
   * @return {@code true} if "{@code dotnet build-server shutdown}" should be run after the main command; {@code false} otherwise.
   */
  public boolean isShutDownBuildServers() {
    return this.shutDownBuildServers;
  }

  /**
   * Determines whether or not any build servers started by the main command should be shut down.
   *
   * @param shutDownBuildServers {@code true} if "{@code dotnet build-server shutdown}" should be run after the main command;
   *                             {@code false} otherwise.
   */
  @DataBoundSetter
  public void setShutDownBuildServers(boolean shutDownBuildServers) {
    this.shutDownBuildServers = shutDownBuildServers;
  }

  /**
   * Determines whether or not the presence of warnings makes the build unstable.
   *
   * @return {@code true} if warnings cause the build to be marked as unstable; {@code false} otherwise.
   */
  public boolean isUnstableIfWarnings() {
    return this.unstableIfWarnings;
  }

  /**
   * Determines whether or not the presence of warnings makes the build unstable.
   *
   * @param unstableIfWarnings {@code true} if warnings cause the build to be marked as unstable; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setUnstableIfWarnings(boolean unstableIfWarnings) {
    this.unstableIfWarnings = unstableIfWarnings;
  }

  /** The verbosity to use for the command. */
  @CheckForNull
  protected String verbosity;

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

}
