package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/** A build step using the 'dotnet' executable to run unit tests for a project, using its configured runner. */
public final class DotNetTest extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetTest() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull List<String> args) {
    args.add("test");
  }

  //region Properties

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetTest")
  public static class DescriptorImpl extends DotNetUsingMSBuild.DescriptorImpl {

    public DescriptorImpl() {
      load();
    }

    protected DescriptorImpl(Class<? extends DotNetTest> clazz) {
      super(clazz);
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetTest_DisplayName();
    }

  }

  //endregion

}
