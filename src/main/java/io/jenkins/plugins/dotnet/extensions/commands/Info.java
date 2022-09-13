package io.jenkins.plugins.dotnet.extensions.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class Info extends Command {

  private static final long serialVersionUID = 4618580464187335911L;

  @DataBoundConstructor
  public Info() {
  }

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    args.addFlag("info");
  }

  @Extension
  @Symbol("info")
  public static final class DescriptorImpl extends CommandDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Show Installation Information (dotnet --info)";
    }

  }

}
