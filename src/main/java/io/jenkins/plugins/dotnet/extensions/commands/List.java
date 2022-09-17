package io.jenkins.plugins.dotnet.extensions.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.listWhat.ListWhat;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class List extends Command {

  private static final long serialVersionUID = -6082623118113171310L;

  @DataBoundConstructor
  public List(@NonNull ListWhat what) {
    this.what = what;
  }

  //region Properties

  @NonNull
  private final ListWhat what;

  @NonNull
  public ListWhat getWhat() {
    return this.what;
  }

  //endregion

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption("list");
    this.what.addCommandLineArguments(arguments);
  }

  @Extension
  @Symbol("list")
  public static final class DescriptorImpl extends CommandDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.List_DisplayName();
    }

  }

}
