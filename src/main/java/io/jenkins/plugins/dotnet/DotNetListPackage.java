package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractProject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/** A build step using the 'dotnet' executable to list all resolved package dependencies for a project. */
public final class DotNetListPackage extends DotNet {

  @DataBoundConstructor
  public DotNetListPackage() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("list");
    args.add("package");
  }

  //region Properties

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetListPackage")
  public static class DescriptorImpl extends DotNet.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetListPackage> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetListPackage_DisplayName();
    }

  }

  //endregion

}
