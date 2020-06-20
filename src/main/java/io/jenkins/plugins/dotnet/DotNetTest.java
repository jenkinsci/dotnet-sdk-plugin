package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A build step using the 'dotnet' executable to run unit tests for a project, using its configured runner. */
public final class DotNetTest extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetTest() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
    args.add("test");
    super.addCommandLineArguments(args, resolver, sensitive);
    if (this.framework != null)
      args.add("-f:" + this.framework);
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
    if (this.blame)
      args.add("--blame");
    if (this.collect != null)
      args.add("--collect", this.collect);
    if (this.diag != null)
      args.add("--diag", this.diag);
    if (this.filter != null)
      args.add("--filter", this.filter);
    if (this.listTests)
      args.add("--list-tests");
    if (this.logger != null)
      args.add("--logger", this.logger);
    if (this.noBuild)
      args.add("--no-build");
    if (this.noRestore)
      args.add("--no-restore");
    if (this.resultsDirectory != null)
      args.add("--results-directory", this.resultsDirectory);
    if (this.settings != null)
      args.add("--settings", this.settings);
    if (this.testAdapterPath != null)
      args.add("--test-adapter-path", this.testAdapterPath);
    // This has to be at the end
    if (this.runSettings != null) {
      args.add("--");
      try {
        args.addKeyValuePairsFromPropertyString("", this.runSettings, resolver, sensitive);
      }
      catch (IOException e) {
        DotNetTest.LOGGER.log(Level.FINE, Messages.DotNetTest_BadRunSettings(), e);
      }
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DotNetTest.class.getName());

  //region Properties

  private boolean blame;

  public boolean isBlame() {
    return this.blame;
  }

  @DataBoundSetter
  public void setBlame(boolean blame) {
    this.blame = blame;
  }

  private String collect;

  public String getCollect() {
    return this.collect;
  }

  @DataBoundSetter
  public void setCollect(String collect) {
    this.collect = Util.fixEmptyAndTrim(collect);
  }

  private String diag;

  public String getDiag() {
    return this.diag;
  }

  @DataBoundSetter
  public void setDiag(String diag) {
    this.diag = Util.fixEmptyAndTrim(diag);
  }

  private String filter;

  public String getFilter() {
    return this.filter;
  }

  @DataBoundSetter
  public void setFilter(String filter) {
    this.filter = Util.fixEmptyAndTrim(filter);
  }

  private String framework;

  public String getFramework() {
    return this.framework;
  }

  @DataBoundSetter
  public void setFramework(String framework) {
    this.framework = Util.fixEmptyAndTrim(framework);
  }

  private boolean listTests;

  public boolean isListTests() {
    return this.listTests;
  }

  @DataBoundSetter
  public void setListTests(boolean listTests) {
    this.listTests = listTests;
  }

  private String logger;

  public String getLogger() {
    return this.logger;
  }

  @DataBoundSetter
  public void setLogger(String logger) {
    this.logger = Util.fixEmptyAndTrim(logger);
  }

  private boolean noBuild;

  public boolean isNoBuild() {
    return this.noBuild;
  }

  @DataBoundSetter
  public void setNoBuild(boolean noBuild) {
    this.noBuild = noBuild;
  }

  private boolean noRestore;

  public boolean isNoRestore() {
    return this.noRestore;
  }

  @DataBoundSetter
  public void setNoRestore(boolean noRestore) {
    this.noRestore = noRestore;
  }

  private String resultsDirectory;

  public String getResultsDirectory() {
    return this.resultsDirectory;
  }

  @DataBoundSetter
  public void setResultsDirectory(String resultsDirectory) {
    this.resultsDirectory = Util.fixEmptyAndTrim(resultsDirectory);
  }

  private String runSettings;

  public String getRunSettings() {
    return this.runSettings;
  }

  @DataBoundSetter
  public void setRunSettings(String runSettings) {
    this.runSettings = Util.fixEmpty(runSettings);
  }

  private String runtime;

  public String getRuntime() {
    return this.runtime;
  }

  @DataBoundSetter
  public void setRuntime(String runtime) {
    this.runtime = Util.fixEmptyAndTrim(runtime);
  }

  private String settings;

  public String getSettings() {
    return this.settings;
  }

  @DataBoundSetter
  public void setSettings(String settings) {
    this.settings = Util.fixEmptyAndTrim(settings);
  }

  private String testAdapterPath;

  public String getTestAdapterPath() {
    return this.testAdapterPath;
  }

  @DataBoundSetter
  public void setTestAdapterPath(String testAdapterPath) {
    this.testAdapterPath = Util.fixEmptyAndTrim(testAdapterPath);
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetTest")
  public static class DescriptorImpl extends MSBuildCommandDescriptor {

    public DescriptorImpl() {
      this.load();
    }

    protected DescriptorImpl(Class<? extends DotNetTest> clazz) {
      super(clazz);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckRunSettings(@QueryParameter String value) {
      try {
        new Properties().load(new StringReader(value));
      }
      catch (Throwable t) {
        return FormValidation.error(t, Messages.DotNetTest_InvalidRunSettings());
      }
      return FormValidation.ok();
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetTest_DisplayName();
    }

  }

  //endregion

}
