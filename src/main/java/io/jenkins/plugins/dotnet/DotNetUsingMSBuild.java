package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DotNetUsingMSBuild extends DotNet {

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
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
    if (this.project != null)
      args.add(this.project);
    if (this.configuration != null)
      args.add("-c:" + this.configuration);
    if (this.properties != null) {
      Properties props = new Properties();
      try {
        props.load(new StringReader(this.properties));
      }
      catch (IOException e) {
        LOGGER.log(Level.FINE, "Failed to load configured MSBuild properties.", e);
      }
      for (Map.Entry<Object, Object> prop : props.entrySet())
        args.add("-p:" + prop.getKey() + "=" + prop.getValue());
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

  protected String options;

  public String getOptions() {
    return this.options;
  }

  @DataBoundSetter
  public void setOptions(String options) {
    this.options = Util.fixEmptyAndTrim(options);
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

  protected boolean noLogo;

  public boolean isNoLogo() {
    return this.noLogo;
  }

  @DataBoundSetter
  public void setNoLogo(boolean noLogo) {
    this.noLogo = noLogo;
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

    public final ComboBoxModel doFillConfigurationItems() {
      final ComboBoxModel model = new ComboBoxModel();
      model.add("Debug");
      model.add("Release");
      return model;
    }

    public final ListBoxModel doFillVerbosityItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_Verbosity_Default(), null);
      model.add(Messages.DotNet_Verbosity_Quiet(), "q");
      model.add(Messages.DotNet_Verbosity_Minimal(), "m");
      model.add(Messages.DotNet_Verbosity_Normal(), "n");
      model.add(Messages.DotNet_Verbosity_Detailed(), "d");
      model.add(Messages.DotNet_Verbosity_Diagnostic(), "diag");
      return model;
    }

  }

  //endregion

}