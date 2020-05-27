package io.jenkins.plugins.dotnet;

import hudson.Extension;
import hudson.Util;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Set;

/** A build step using the 'dotnet' executable to create a NuGet package for a project. */
public final class DotNetClean extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetClean() {
  }

  @Override
  protected void addCommandLineArguments(@Nonnull ArgumentListBuilder args, @Nonnull VariableResolver<String> resolver, @Nonnull Set<String> sensitive) {
    args.add("clean");
    super.addCommandLineArguments(args, resolver, sensitive);
    if (this.framework != null)
      args.add("-f:" + this.framework);
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
  }

  //region Properties

  private String framework;

  public String getFramework() {
    return this.framework;
  }

  @DataBoundSetter
  public void setFramework(String framework) {
    this.framework = Util.fixEmptyAndTrim(framework);
  }

  private String runtime;

  public String getRuntime() {
    return this.runtime;
  }

  @DataBoundSetter
  public void setRuntime(String runtime) {
    this.runtime = Util.fixEmptyAndTrim(runtime);
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetClean")
  public static class DescriptorImpl extends DotNetUsingMSBuild.DescriptorImpl {

    public DescriptorImpl() {
      this.load();
    }

    protected DescriptorImpl(Class<? extends DotNetClean> clazz) {
      super(clazz);
    }

    @Nonnull
    public String getDisplayName() {
      return Messages.DotNetClean_DisplayName();
    }

  }

  //endregion

}
