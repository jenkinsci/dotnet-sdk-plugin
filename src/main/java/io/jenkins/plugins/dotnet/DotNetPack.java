package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/** A build step using the 'dotnet' executable to create a NuGet package for a project. */
public final class DotNetPack extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetPack() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("pack");
  }

  //region Properties

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetPack")
  public static class DescriptorImpl extends DotNetUsingMSBuild.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetPack> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetPack_DisplayName();
    }

  }

  //endregion

}
