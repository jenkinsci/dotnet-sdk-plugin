package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Item;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetBuildStepDescriptor;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import org.jenkinsci.plugins.structs.describable.CustomDescribableModel;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.nio.charset.Charset;
import java.util.Set;

/** A descriptor for a .NET command. */
public abstract class CommandDescriptor extends BuildStepDescriptor<Builder>
  implements CustomDescribableModel, DotNetBuildStepDescriptor {

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
