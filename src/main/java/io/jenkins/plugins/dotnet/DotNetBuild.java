package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/** A build step using the 'dotnet' executable to build a project. */
public final class DotNetBuild extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetBuild() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("build");
    super.addCommandLineArguments(args);
  }

  //region Properties

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetBuild")
  public static class DescriptorImpl extends DotNetUsingMSBuild.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetBuild> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetBuild_DisplayName();
    }

  }

  //endregion

}
