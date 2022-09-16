package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.jenkins.plugins.dotnet.CommonDescriptorMethods;
import org.jenkinsci.plugins.structs.describable.CustomDescribableModel;

/** A descriptor for a .NET command. */
public abstract class CommandDescriptor extends BuildStepDescriptor<Builder>
  implements CustomDescribableModel, CommonDescriptorMethods {

  /**
   * Creates a new .NET command descriptor instance.
   * <p>
   * This version works when you follow the common convention, where a descriptor is written as the static nested class of the
   * describable class.
   */
  protected CommandDescriptor() {
  }

  /**
   * Creates a new .NET command descriptor instance for a specific class.
   *
   * @param clazz The class implementing the command described by this descriptor instance.
   */
  protected CommandDescriptor(@NonNull Class<? extends Command> clazz) {
    super(clazz);
  }

  /**
   * Determines whether this descriptor is applicable for the specified job type.
   *
   * @param jobType The job type.
   *
   * @return {@code true}.
   */
  public final boolean isApplicable(@CheckForNull Class<? extends AbstractProject> jobType) {
    return false;
  }

}
