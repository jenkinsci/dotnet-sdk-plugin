package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetSDK;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import org.jenkinsci.plugins.structs.describable.CustomDescribableModel;
import org.kohsuke.stapler.QueryParameter;

/** A descriptor for a .NET command. */
public abstract class CommandDescriptor extends BuildStepDescriptor<Builder> implements CustomDescribableModel {

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
   * Performs auto-completion for a .NET target framework moniker.
   *
   * @param value The (partial) value to perform auto-completion for.
   *
   * @return The computed auto-completion candidates.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final AutoCompletionCandidates doAutoCompleteFramework(@CheckForNull @QueryParameter String value) {
    return Framework.getInstance().autoCompleteMoniker(value);
  }

  /**
   * Performs auto-completion for a list of .NET target framework monikers.
   *
   * @param value The (partial) value to perform auto-completion for.
   *
   * @return The computed auto-completion candidates.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final AutoCompletionCandidates doAutoCompleteFrameworks(@CheckForNull @QueryParameter String value) {
    return Framework.getInstance().autoCompleteMoniker(value);
  }

  /**
   * Performs auto-completion for a .NET target runtime identifier.
   *
   * @param value The (partial) value to perform auto-completion for.
   *
   * @return The computed auto-completion candidates.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final AutoCompletionCandidates doAutoCompleteRuntime(@CheckForNull @QueryParameter String value) {
    return Runtime.getInstance().autoCompleteIdentifier(value);
  }

  /**
   * Performs auto-completion for a list of .NET runtime identifiers.
   *
   * @param value The (partial) value to perform auto-completion for.
   *
   * @return The computed auto-completion candidates.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final AutoCompletionCandidates doAutoCompleteRuntimes(@CheckForNull @QueryParameter String value) {
    return Runtime.getInstance().autoCompleteIdentifier(value);
  }

  /**
   * Performs validation on a .NET target framework moniker.
   *
   * @param value The value to validate.
   *
   * @return The result of the validation.
   */
  @SuppressWarnings("unused")
  @NonNull
  public FormValidation doCheckFramework(@CheckForNull @QueryParameter String value) {
    return Framework.getInstance().checkMoniker(value);
  }

  /**
   * Performs validation on a list of .NET target framework monikers.
   *
   * @param value The value to validate.
   *
   * @return The result of the validation.
   */
  @SuppressWarnings("unused")
  @NonNull
  public FormValidation doCheckFrameworks(@CheckForNull @QueryParameter String value) {
    return Framework.getInstance().checkMonikers(value);
  }

  /**
   * Performs validation on a .NET runtime identifier.
   *
   * @param value The value to validate.
   *
   * @return The result of the validation.
   */
  @SuppressWarnings("unused")
  @NonNull
  public FormValidation doCheckRuntime(@CheckForNull @QueryParameter String value) {
    return Runtime.getInstance().checkIdentifier(value);
  }

  /**
   * Performs validation on a list of .NET runtime identifiers.
   *
   * @param value The values to validate.
   *
   * @return The result of the validation.
   */
  @SuppressWarnings("unused")
  @NonNull
  public FormValidation doCheckRuntimes(@CheckForNull @QueryParameter String value) {
    return Runtime.getInstance().checkIdentifiers(value);
  }

  /**
   * Fills a listbox with the names of .NET SDKs that have been defined as global tools.
   *
   * @return A suitably filled listbox model.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final ListBoxModel doFillSdkItems() {
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.Command_DefaultSDK(), "");
    DotNetSDK.addSdks(model);
    return model;
  }

  /**
   * Fills a listbox with the possible values for the .NET CLI "verbosity" option.
   *
   * @return A suitably filled listbox model.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final ListBoxModel doFillVerbosityItems() {
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.Command_Verbosity_Default(), "");
    model.add(Messages.Command_Verbosity_Quiet(), "q");
    model.add(Messages.Command_Verbosity_Minimal(), "m");
    model.add(Messages.Command_Verbosity_Normal(), "n");
    model.add(Messages.Command_Verbosity_Detailed(), "d");
    model.add(Messages.Command_Verbosity_Diagnostic(), "diag");
    return model;
  }

  /**
   * Gets the button text to use for the "Advanced" button.
   *
   * @return "More Options", or the localized equivalent.
   */
  @SuppressWarnings("unused")
  @NonNull
  public final String getMoreOptions() {
    return Messages.Command_MoreOptions();
  }

  /**
   * Determines whether or not this descriptor is applicable for the specified job type.
   *
   * @param jobType The job type.
   *
   * @return {@code true}.
   */
  public final boolean isApplicable(@CheckForNull Class<? extends AbstractProject> jobType) {
    return true;
  }

}
