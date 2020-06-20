package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
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

public abstract class DotNetUsingMSBuild extends DotNet {

  @Override
  protected void addCommandLineArguments(@NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
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

  protected String configuration;

  public String getConfiguration() {
    return this.configuration;
  }

  @DataBoundSetter
  public void setConfiguration(String configuration) {
    this.configuration = Util.fixEmptyAndTrim(configuration);
  }

  protected boolean noLogo;

  public boolean isNoLogo() {
    return this.noLogo;
  }

  @DataBoundSetter
  public void setNoLogo(boolean noLogo) {
    this.noLogo = noLogo;
  }

  protected String options;

  public String getOptions() {
    return this.options;
  }

  @DataBoundSetter
  public void setOptions(String options) {
    this.options = Util.fixEmptyAndTrim(options);
  }

  protected String outputDirectory;

  public String getOutputDirectory() {
    return this.outputDirectory;
  }

  @DataBoundSetter
  public void setOutputDirectory(String outputDirectory) {
    this.outputDirectory = Util.fixEmptyAndTrim(outputDirectory);
  }

  protected String project;

  public String getProject() {
    return this.project;
  }

  @DataBoundSetter
  public void setProject(String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  protected String properties;

  public String getProperties() {
    return this.properties;
  }

  @DataBoundSetter
  public void setProperties(String properties) {
    this.properties = Util.fixEmpty(properties);
  }

  public boolean isShutDownBuildServers() {
    return this.shutDownBuildServers;
  }

  @DataBoundSetter
  public void setShutDownBuildServers(boolean shutDownBuildServers) {
    this.shutDownBuildServers = shutDownBuildServers;
  }

  public boolean isUnstableIfWarnings() {
    return this.unstableIfWarnings;
  }

  @DataBoundSetter
  public void setUnstableIfWarnings(boolean unstableIfWarnings) {
    this.unstableIfWarnings = unstableIfWarnings;
  }

  protected String verbosity;

  public String getVerbosity() {
    return this.verbosity;
  }

  @DataBoundSetter
  public void setVerbosity(String verbosity) {
    this.verbosity = Util.fixEmptyAndTrim(verbosity);
  }

  //endregion

  //region DescriptorImpl

  public static abstract class DescriptorImpl extends DotNet.DescriptorImpl {

    protected DescriptorImpl() {
    }

    protected DescriptorImpl(Class<? extends DotNetUsingMSBuild> clazz) {
      super(clazz);
    }

    @SuppressWarnings("unused")
    public FormValidation doCheckProperties(@QueryParameter String value) {
      try {
        new Properties().load(new StringReader(value));
      }
      catch (Throwable t) {
        return FormValidation.error(t, Messages.DotNetUsingMSBuild_InvalidProperties());
      }
      return FormValidation.ok();
    }

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
