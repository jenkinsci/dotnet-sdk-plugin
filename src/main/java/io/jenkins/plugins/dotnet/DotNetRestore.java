package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/** A build step using the 'dotnet' executable to restore dependencies for a project. */
public final class DotNetRestore extends DotNet {

  @DataBoundConstructor
  public DotNetRestore() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("restore");
  }

  //region Properties

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetRestore")
  public static class DescriptorImpl extends DotNet.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetRestore> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetRestore_DisplayName();
    }

  }

  //endregion

}
