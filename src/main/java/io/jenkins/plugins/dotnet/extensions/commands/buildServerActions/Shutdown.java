package io.jenkins.plugins.dotnet.extensions.commands.buildServerActions;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public final class Shutdown extends BuildServerAction {

  private static final long serialVersionUID = 3291344502893115786L;

  @DataBoundConstructor
  public Shutdown() {
  }

  //region Properties

  private boolean msBuild;

  public boolean isMsBuild() {
    return this.msBuild;
  }

  @DataBoundSetter
  public void setMsBuild(boolean yes) {
    this.msBuild = yes;
  }

  private boolean razor;

  public boolean isRazor() {
    return this.razor;
  }

  @DataBoundSetter
  public void setRazor(boolean yes) {
    this.razor = yes;
  }

  private boolean vbCsCompiler;

  public boolean isVbCsCompiler() {
    return this.vbCsCompiler;
  }

  @DataBoundSetter
  public void setVbCsCompiler(boolean yes) {
    this.vbCsCompiler = yes;
  }

  //endregion

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption("shutdown");
    arguments.addFlag("msbuild", this.msBuild);
    arguments.addFlag("razor", this.razor);
    arguments.addFlag("vbcscompiler", this.vbCsCompiler);
  }

  @Extension
  @Symbol("shutdown")
  public static final class DescriptorImpl extends BuildServerActionDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.BuildServer_Shutdown_DisplayName();
    }

  }

}
