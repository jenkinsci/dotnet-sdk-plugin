package io.jenkins.plugins.dotnet.extensions.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.buildServerActions.BuildServerAction;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class BuildServer extends Command {

  private static final long serialVersionUID = 9076872510479045L;

  @DataBoundConstructor
  public BuildServer(@NonNull BuildServerAction action) {
    this.action = action;
  }

  //region Properties

  @NonNull
  private final BuildServerAction action;

  @NonNull
  public BuildServerAction getAction() {
    return this.action;
  }

  //endregion


  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption("build-server");
    this.action.addCommandLineArguments(arguments);
  }

  @Extension
  @Symbol("list")
  public static final class DescriptorImpl extends CommandDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.BuildServer_DisplayName();
    }

  }

}
