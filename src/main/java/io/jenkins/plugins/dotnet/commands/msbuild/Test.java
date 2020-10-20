package io.jenkins.plugins.dotnet.commands.msbuild;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.structs.describable.UninstantiatedDescribable;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A build step to run "{@code dotnet test}", running unit tests for a project, using its configured runner. */
public final class Test extends MSBuildCommand {

  /** Creates a new "{@code dotnet test}" build step. */
  @DataBoundConstructor
  public Test() {
    super("test");
  }

  /**
   * Adds command line arguments for this "{@code dotnet test}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link MSBuildCommand#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>{@code -f:xxx}, if a target framework moniker has been specified via {@link #setFramework(String)}.</li>
   *   <li>{@code -r:xxx}, if a runtime identifier has been specified via {@link #setRuntime(String)}.</li>
   *   <li>{@code --blame}, if requested via {@link #setBlame(boolean)}.</li>
   *   <li>{@code --collect xxx}, if a data collector has been specified via {@link #setCollect(String)}.</li>
   *   <li>{@code --diag xxx}, if a diagnostics file has been specified via {@link #setDiag(String)}.</li>
   *   <li>{@code --filter xxx}, if a filter expression has been specified via {@link #setFilter(String)}.</li>
   *   <li>{@code --list-tests}, if requested via {@link #setListTests(boolean)}.</li>
   *   <li>{@code --logger xxx}, if a logger has been specified via {@link #setLogger(String)}.</li>
   *   <li>{@code --no-build}, if requested via {@link #setNoBuild(boolean)}.</li>
   *   <li>{@code --no-restore}, if requested via {@link #setNoRestore(boolean)}.</li>
   *   <li>{@code --results-directory xxx}, if a results directory has been specified via {@link #setResultsDirectory(String)}.</li>
   *   <li>{@code --settings xxx}, if a {@code .runsettings} file has been specified via {@link #setSettings(String)}.</li>
   *   <li>{@code --test-adapter-path xxx}, if a test adapter path has been specified via {@link #setTestAdapterPath(String)}.</li>
   *   <li>
   *     {@code -- name=value [name=value]}, for all settings specified via {@link #setRunSettings(Map)} or
   *     {@link #setRunSettingsString(String)}.
   *   </li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) {
    super.addCommandLineArguments(args);
    args.addOption('f', this.framework);
    args.addOption('r', this.runtime);
    args.addFlag("blame", this.blame);
    args.addOption("collect", this.collect);
    args.addOption("diag", this.diag);
    args.addOption("filter", this.filter);
    args.addFlag("list-tests", this.listTests);
    args.addOption("logger", this.logger);
    args.addFlag("no-build", this.noBuild);
    args.addFlag("no-restore", this.noRestore);
    args.addOption("results-directory", this.resultsDirectory);
    args.addOption("settings", this.settings);
    args.addOption("test-adapter-path", this.testAdapterPath);
    // This has to be at the end
    if (this.runSettings != null) {
      args.add("--");
      try {
        args.addPropertyOptions("", this.runSettings);
      }
      catch (IOException e) {
        Test.LOGGER.log(Level.FINE, Messages.MSBuild_Test_BadRunSettings(), e);
      }
    }
  }

  private static final Logger LOGGER = Logger.getLogger(Test.class.getName());

  //region Properties

  private boolean blame;

  /**
   * Determines whether or not tests should be run in blame mode, to diagnost test host crashes.
   *
   * @return {@code true} when tests are run in blame mode; {@code false} otherwise.
   */
  public boolean isBlame() {
    return this.blame;
  }

  /**
   * Determines whether or not tests should be run in blame mode, to diagnost test host crashes.
   *
   * @param blame {@code true} to run tests in blame mode; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setBlame(boolean blame) {
    this.blame = blame;
  }

  private String collect;

  /**
   * Gets the data collector to use.
   *
   * @return The data collector to use.
   */
  @CheckForNull
  public String getCollect() {
    return this.collect;
  }

  /**
   * Sets the data collector to use.
   *
   * @param collect The data collector to use.
   */
  @DataBoundSetter
  public void setCollect(@CheckForNull String collect) {
    this.collect = Util.fixEmptyAndTrim(collect);
  }

  private String diag;

  /**
   * Gets the path to the diagnostics file to use.
   *
   * @return The path to the diagnostics file to use.
   */
  @CheckForNull
  public String getDiag() {
    return this.diag;
  }

  /**
   * Sets the path to the diagnostics file to use.
   *
   * @param diag The path to the diagnostics file to use.
   */
  @DataBoundSetter
  public void setDiag(@CheckForNull String diag) {
    this.diag = Util.fixEmptyAndTrim(diag);
  }

  private String filter;

  /**
   * Gets the filter expression to use.
   *
   * @return The filter expression to use.
   */
  @CheckForNull
  public String getFilter() {
    return this.filter;
  }

  /**
   * Sets the filter expression to use.
   *
   * @param filter The filter expression to use.
   */
  @DataBoundSetter
  public void setFilter(@CheckForNull String filter) {
    this.filter = Util.fixEmptyAndTrim(filter);
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

  private boolean listTests;

  /**
   * Determines whether or not discovered tests should be listed.
   *
   * @return {@code true} when discovered tests are listed; {@code false} otherwise.
   */
  public boolean isListTests() {
    return this.listTests;
  }

  /**
   * Determines whether or not discovered tests should be listed.
   *
   * @param listTests {@code true} to list discovered tests; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setListTests(boolean listTests) {
    this.listTests = listTests;
  }

  private String logger;

  /**
   * Gets the logger to use.
   *
   * @return The logger to use.
   */
  @CheckForNull
  public String getLogger() {
    return this.logger;
  }

  /**
   * Sets the logger to use.
   *
   * @param logger The logger to use.
   */
  @DataBoundSetter
  public void setLogger(@CheckForNull String logger) {
    this.logger = Util.fixEmptyAndTrim(logger);
  }

  private boolean noBuild;

  /**
   * Determines whether or not a build should be performed before running tests.
   *
   * @return {@code true} when neither a restore nor a build will be performed before running tests; {@code false} otherwise.
   */
  public boolean isNoBuild() {
    return this.noBuild;
  }

  /**
   * Determines whether or not a build should be performed before running tests.
   *
   * @param noBuild {@code true} to perform neither a restore nor a build before running tests; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoBuild(boolean noBuild) {
    this.noBuild = noBuild;
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

  private String resultsDirectory;

  /**
   * Gets the directory to store test results in.
   *
   * @return The directory to store test results in.
   */
  @CheckForNull
  public String getResultsDirectory() {
    return this.resultsDirectory;
  }

  /**
   * Sets the directory to store test results in.
   *
   * @param resultsDirectory The directory to store test results in.
   */
  @DataBoundSetter
  public void setResultsDirectory(@CheckForNull String resultsDirectory) {
    this.resultsDirectory = Util.fixEmptyAndTrim(resultsDirectory);
  }

  private String runSettings;

  /**
   * Gets the inline run settings to use.
   *
   * @return The inline run settings to use.
   *
   * @throws IOException When there is a problem loading the run settings into a Java {@link Properties} object.
   */
  @CheckForNull
  public Map<String, String> getRunSettings() throws IOException {
    return DotNetUtils.createPropertyMap(this.runSettings);
  }

  /**
   * Sets the inline run settings to use.
   *
   * @param runSettings The inline run settings to use.
   *
   * @throws IOException When there is a problem using a {@link Properties} object to generate a string form for the map.
   */
  @DataBoundSetter
  public void setRunSettings(@CheckForNull Map<String, String> runSettings) throws IOException {
    this.runSettings = DotNetUtils.createPropertyString(runSettings);
  }

  /**
   * Gets the inline run settings to use.
   *
   * @return The inline run settings to use.
   */
  @CheckForNull
  public String getRunSettingsString() {
    return this.runSettings;
  }

  /**
   * Sets the inline run settings to use.
   *
   * @param runSettings The inline run settings to use.
   */
  @DataBoundSetter
  public void setRunSettingsString(@CheckForNull String runSettings) {
    this.runSettings = Util.fixEmpty(runSettings);
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

  private String settings;

  /**
   * Gets the {@code .runsettings} file to use.
   *
   * @return The {@code .runsettings} file to use.
   */
  @CheckForNull
  public String getSettings() {
    return this.settings;
  }

  /**
   * Sets the {@code .runsettings} file to use.
   *
   * @param settings The {@code .runsettings} file to use.
   */
  @DataBoundSetter
  public void setSettings(@CheckForNull String settings) {
    this.settings = Util.fixEmptyAndTrim(settings);
  }

  private String testAdapterPath;

  /**
   * Gets the path to search for test adapters.
   *
   * @return The path to search for test adapters.
   */
  @CheckForNull
  public String getTestAdapterPath() {
    return this.testAdapterPath;
  }

  /**
   * Sets the path to search for test adapters.
   *
   * @param testAdapterPath The path to search for test adapters.
   */
  @DataBoundSetter
  public void setTestAdapterPath(@CheckForNull String testAdapterPath) {
    this.testAdapterPath = Util.fixEmptyAndTrim(testAdapterPath);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet test}" build steps. */
  @Extension
  @Symbol("dotnetTest")
  public static final class DescriptorImpl extends MSBuildCommandDescriptor {

    /** Creates a new "{@code dotnet test}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Performs (basic) validation on a set of run settings.
     *
     * @param value The run settings to validate.
     *
     * @return The validation result.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckRunSettingsString(@CheckForNull @QueryParameter String value) {
      value = Util.fixEmptyAndTrim(value);
      if (value != null) {
        try {
          Util.loadProperties(value);
        }
        catch (Throwable t) {
          return FormValidation.error(t, Messages.MSBuild_Test_InvalidRunSettings());
        }
      }
      return FormValidation.ok();
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.MSBuild_Test_DisplayName();
    }

    @NonNull
    @Override
    public UninstantiatedDescribable customUninstantiate(@NonNull UninstantiatedDescribable ud) {
      ud = super.customUninstantiate(ud);
      final Map<String, Object> args = new HashMap<>();
      for (final Map.Entry<String, ?> arg : ud.getArguments().entrySet()) {
        final String name = arg.getKey();
        if ("runSettingsString".equals(name))
          continue;
        args.put(name, arg.getValue());
      }
      return new UninstantiatedDescribable(ud.getSymbol(), ud.getKlass(), args);
    }

  }

  //endregion

}
