package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractProject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/** A build step using the 'dotnet' executable to create a NuGet package for a project. */
public final class DotNetPack extends DotNet {

  // TODO: Determine configuration needed.

  @DataBoundConstructor
  public DotNetPack(String sdk) {
    super(sdk, "pack");
  }

  @Extension
  @Symbol("dotnetPack")
  public static class DescriptorImpl extends DotNet.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetPack> clazz) {
      super(clazz);
    }

    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetPack_DisplayName();
    }

  }

}
