package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import io.jenkins.plugins.dotnet.commands.Command;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import org.kohsuke.stapler.DataBoundSetter;

/** A build step executing a subcommand of {@code dotnet nuget}. */
public class NuGetCommand extends Command {

  protected NuGetCommand() {
    this.subCommand = null;
  }

  protected NuGetCommand(@NonNull String subCommand) {
    this.subCommand = subCommand;
  }

  private final String subCommand;

  /**
   * Adds command line arguments for this .NET NuGet command invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code nuget}</li>
   *   <li>The sub-command (e.g. {@code push}), if applicable.</li>
   *   <li>{@code --force-english-output}, if requested via {@link #setForceEnglishOutput(boolean)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    args.add("nuget").addOption(this.subCommand);
    args.addFlag("force-english-output", this.forceEnglishOutput);
  }

  //region Properties

  protected boolean forceEnglishOutput;

  public boolean isForceEnglishOutput() {
    return this.forceEnglishOutput;
  }

  @DataBoundSetter
  public void setForceEnglishOutput(boolean forceEnglishOutput) {
    this.forceEnglishOutput = forceEnglishOutput;
  }

  //endregion

}
