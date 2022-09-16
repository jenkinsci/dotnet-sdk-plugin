package io.jenkins.plugins.dotnet.extensions.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import io.jenkins.plugins.dotnet.DotNetArguments;

public interface CommandLineArgumentProvider {

  /**
   * Adds command line arguments.
   *
   * @param arguments The current set of arguments.
   *
   * @throws AbortException When something goes wrong.
   */
  void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException;

}
