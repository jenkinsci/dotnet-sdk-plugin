package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractProject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/** A build step using the 'dotnet' executable to build a project. */
public final class DotNetBuild extends DotNet {

  @DataBoundConstructor
  public DotNetBuild() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("build");
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
