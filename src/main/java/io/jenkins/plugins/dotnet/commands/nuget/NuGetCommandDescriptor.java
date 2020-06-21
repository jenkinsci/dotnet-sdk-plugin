package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.plugins.dotnet.commands.CommandDescriptor;

/** A descriptor for a subcommand of {@code dotnet nuget}. */
public abstract class NuGetCommandDescriptor extends CommandDescriptor {

  /**
   * Creates a new .NET NuGet command descriptor instance.
   * <p>
   * This version works when you follow the common convention, where a descriptor is written as the static nested class of the
   * describable class.
   */
  protected NuGetCommandDescriptor() {
  }

  /**
   * Creates a new .NET NuGet command descriptor instance for a specific class.
   *
   * @param clazz The class implementing the command described by this descriptor instance.
   */
  protected NuGetCommandDescriptor(@NonNull Class<? extends NuGetCommand> clazz) {
    super(clazz);
  }

}
