package io.jenkins.plugins.dotnet.extensions.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.nugetActions.NuGetAction;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class NuGet extends Command {

  private static final long serialVersionUID = -791201719211101136L;

  @DataBoundConstructor
  public NuGet(@NonNull NuGetAction action) {
    this.action = action;
  }

  //region Properties

  @NonNull
  private final NuGetAction action;

  @NonNull
  public NuGetAction getAction() {
    return this.action;
  }

  //endregion

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption("nuget");
    this.action.addCommandLineArguments(arguments);
  }

  @Extension
  @Symbol("nuget")
  public static final class DescriptorImpl extends CommandDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.NuGet_DisplayName();
    }

  }

}
