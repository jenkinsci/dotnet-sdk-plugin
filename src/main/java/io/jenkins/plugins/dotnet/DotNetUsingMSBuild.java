package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A build step executing an MSBuild-based .NET CLI command. */
public abstract class DotNetUsingMSBuild extends DotNet {

  /**
   * {@inheritDoc}
   * <p>
   * This adds:
   * <ol>
   *   <li>Any raw options specified via {@link #setOptions(String)}.</li>
   *   <li>{@code --nologo}, if requested via {@link #setNoLogo(boolean)}.</li>
   *   <li>{@code -v:xxx}, if a verbosity has been specified via {@link #setVerbosity(String)}.</li>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>{@code --output xxx}, if an output directory has been specified via {@link #setOutputDirectory(String)}.</li>
   *   <li>{@code -c:xxx}, if a configuration has been specified via {@link #setConfiguration(String)}.</li>
   *   <li>{@code -p:name=value}, for all properties specified via {@link #setProperties(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
    if (this.options != null) {
      for (String option : Util.tokenize(this.options)) {
        option = Util.fixEmptyAndTrim(option);
        if (option != null)
          args.add(option);
      }
    }
    if (this.noLogo)
      args.add("--nologo");
    if (this.verbosity != null)
      args.add("-v:" + this.verbosity);
    args.add(this.project);
    if (this.outputDirectory != null)
      args.add("--output", this.outputDirectory);
    if (this.configuration != null)
      args.add("-c:" + this.configuration);
    try {
      args.addKeyValuePairsFromPropertyString("-p:", this.properties, resolver, sensitive);
    }
    catch (IOException e) {
      DotNetUsingMSBuild.LOGGER.log(Level.FINE, Messages.DotNetUsingMSBuild_BadProperties(), e);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DotNetUsingMSBuild.class.getName());

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
  protected boolean noLogo;

  /**
   * Determines whether or not the MSBuild version/copyright lines should be suppressed.
   *
   * @return {@code true} if the MSBuild startup banner should be suppressed; {@code false} otherwise.
   */
  public boolean isNoLogo() {
    return this.noLogo;
  }

  /**
   * Determines whether or not the MSBuild version/copyright lines should be suppressed.
   *
   * @param noLogo {@code true} if the MSBuild startup banner should be suppressed; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoLogo(boolean noLogo) {
    this.noLogo = noLogo;
  }

  /** Additional options to pass to the command. */
  @CheckForNull
  protected String options;

  /**
   * Gets additional options to pass to the command.
   * Options specified via more specific settings will take precedence over options specified here.
   *
   * @return Additional options to pass to the command.
   */
  @CheckForNull
  public String getOptions() {
    return this.options;
  }

  /**
   * Sets additional options to pass to the command.
   * Options specified via more specific settings will take precedence over options specified here.
   *
   * @param options Additional options to pass to the command.
   */
  @DataBoundSetter
  public void setOptions(@CheckForNull String options) {
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
  public String getProperties() {
    return this.properties;
  }

  /**
   * Sets MSBuild properties to be applied to the command.
   *
   * @param properties MSBuild properties to be applied to the command (one key=value setting per line).
   */
  @DataBoundSetter
  public void setProperties(@CheckForNull String properties) {
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

  //region MSBuild Command Descriptor

  /** A descriptor for an MSBuild-based .NET command. */
  public static abstract class MSBuildCommandDescriptor extends CommandDescriptor {

    /**
     * Creates a new .NET MSBuild command descriptor instance.
     * <p>
     * This version works when you follow the common convention, where a descriptor is written as the static nested class of the
     * describable class.
     */
    protected MSBuildCommandDescriptor() {
    }

    /**
     * Creates a new .NET MSBuild command descriptor instance for a specific class.
     *
     * @param clazz The class implementing the command described by this descriptor instance.
     */
    protected MSBuildCommandDescriptor(@NonNull Class<? extends DotNetUsingMSBuild> clazz) {
      super(clazz);
    }

    /**
     * Performs validation on a set of MSBuild properties.
     *
     * @param value The value to validate.
     *
     * @return The result of the validation.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckProperties(@QueryParameter String value) {
      try {
        new Properties().load(new StringReader(value));
      }
      catch (Throwable t) {
        return FormValidation.error(t, Messages.DotNetUsingMSBuild_InvalidProperties());
      }
      return FormValidation.ok();
    }

    /**
     * Fills a combobox with standard MSBuild configuration names.
     *
     * @return A suitable filled combobox model.
     */
    @SuppressWarnings("unused")
    public final ComboBoxModel doFillConfigurationItems() {
      final ComboBoxModel model = new ComboBoxModel();
      // Note: not localized
      model.add("Debug");
      model.add("Release");
      return model;
    }

  }

  //endregion

}
