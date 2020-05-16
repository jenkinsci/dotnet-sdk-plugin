package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractProject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/** A build step using the 'dotnet' executable to build a project. */
public final class DotNetBuild extends DotNet {

  private final String configuration;

  private final String project;

  private final String targets;

  private final String properties;

  private final String verbosity;

  private final boolean binLog;

  @DataBoundConstructor
  public DotNetBuild(String sdk, String configuration, String project, String targets, String properties, String verbosity, boolean binLog) {
    super(sdk, "build");
    this.configuration = Util.fixEmpty(configuration);
    this.project = Util.fixEmpty(project);
    this.targets = targets;
    this.properties = Util.fixEmpty(properties);
    this.verbosity = Util.fixEmpty(verbosity);
    this.binLog = binLog;
  }

  public String getConfiguration() {
    return configuration;
  }

  public String getProject() {
    return project;
  }

  public String getTargets() {
    return targets;
  }

  public String getProperties() {
    return properties;
  }

  public String getVerbosity() {
    return verbosity;
  }

  public boolean isBinLog() {
    return binLog;
  }

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetBuild")
  public static class DescriptorImpl extends DotNet.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetBuild> clazz) {
      super(clazz);
    }

    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetBuild_DisplayName();
    }

  }

  //endregion

}
