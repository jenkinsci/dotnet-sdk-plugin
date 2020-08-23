package io.jenkins.plugins.dotnet.commands.tool;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.plugins.dotnet.commands.CommandDescriptor;

/** A descriptor for a subcommand of {@code dotnet tool}. */
public abstract class ToolCommandDescriptor extends CommandDescriptor {

  /**
   * Creates a new .NET tool command descriptor instance.
   * <p>
   * This version works when you follow the common convention, where a descriptor is written as the static nested class of the
   * describable class.
   */
  protected ToolCommandDescriptor() {
  }

  /**
   * Creates a new .NET tool command descriptor instance for a specific class.
   *
   * @param clazz The class implementing the command described by this descriptor instance.
   */
  protected ToolCommandDescriptor(@NonNull Class<? extends ToolCommand> clazz) {
    super(clazz);
  }

}
