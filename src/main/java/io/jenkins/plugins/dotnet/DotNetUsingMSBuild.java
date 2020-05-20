package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.List;

public abstract class DotNetUsingMSBuild extends DotNet {

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    if (this.noLogo)
      args.add("--nologo");
    if (this.verbosity != null)
      args.add("-v:" + this.verbosity);
    if (this.project != null)
      args.add(this.project);
    if (this.configuration != null)
      args.add("-c:" + this.configuration);
  }

  //region Properties

  protected String configuration;

  public String getConfiguration() {
    return this.configuration;
  }

  @DataBoundSetter
  public void setConfiguration(String configuration) {
    this.configuration = Util.fixEmptyAndTrim(configuration);
  }

  protected String project;

  public String getProject() {
    return this.project;
  }

  @DataBoundSetter
  public void setProject(String project) {
    this.project = Util.fixEmptyAndTrim(project);
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
