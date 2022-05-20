package io.jenkins.plugins.dotnet.commands.tool;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import io.jenkins.plugins.dotnet.commands.Command;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;

/** A build step executing a subcommand of {@code dotnet tool}. */
public class ToolCommand extends Command {

  /** Creates a new {@code dotnet nuget} build step. */
  protected ToolCommand() {
    this.subCommand = null;
  }

  /**
   * Creates a new {@code dotnet tool} build step.
   *
   * @param subCommand The {@code dotnet tool} subcommand to execute.
   */
  protected ToolCommand(@NonNull String subCommand) {
    this.subCommand = subCommand;
  }

  private final String subCommand;

  /**
   * Adds command line arguments for this .NET tool command invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code tool}</li>
   *   <li>The sub-command (e.g. {@code restore}), if applicable.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    args.add("tool").addOption(this.subCommand);
  }

}
