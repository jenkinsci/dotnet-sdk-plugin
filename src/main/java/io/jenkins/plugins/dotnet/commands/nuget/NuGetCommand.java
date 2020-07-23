package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import io.jenkins.plugins.dotnet.commands.Command;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import org.kohsuke.stapler.DataBoundSetter;

/** A build step executing a subcommand of {@code dotnet nuget}. */
public abstract class NuGetCommand extends Command {

  /**
   * Adds command line arguments for this .NET NuGet command invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code --force-english-output}, if requested via {@link #setForceEnglishOutput(boolean)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
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
