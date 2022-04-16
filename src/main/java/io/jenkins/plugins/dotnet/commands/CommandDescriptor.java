package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.ExtensionList;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.security.Permission;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetSDK;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import org.jenkinsci.plugins.structs.describable.CustomDescribableModel;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.nio.charset.Charset;
import java.util.Set;

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
   * @param item  The item being configured.
   *
   * @return The computed auto-completion candidates.
   */
  @NonNull
  @POST
  public final AutoCompletionCandidates doAutoCompleteFramework(@CheckForNull @QueryParameter String value,
                                                                @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Framework.getInstance().autoCompleteMoniker(value);
  }

  /**
   * Performs auto-completion for a list of .NET target framework monikers.
   *
   * @param value The (partial) value to perform auto-completion for.
   * @param item  The item being configured.
   *
   * @return The computed auto-completion candidates.
   */
  @NonNull
  @POST
  public final AutoCompletionCandidates doAutoCompleteFrameworksString(@CheckForNull @QueryParameter String value,
                                                                       @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Framework.getInstance().autoCompleteMoniker(value);
  }

  /**
   * Performs auto-completion for a .NET target runtime identifier.
   *
   * @param value The (partial) value to perform auto-completion for.
   * @param item  The item being configured.
   *
   * @return The computed auto-completion candidates.
   */
  @NonNull
  @POST
  public final AutoCompletionCandidates doAutoCompleteRuntime(@CheckForNull @QueryParameter String value,
                                                              @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Runtime.getInstance().autoCompleteIdentifier(value);
  }

  /**
   * Performs auto-completion for a list of .NET runtime identifiers.
   *
   * @param value The (partial) value to perform auto-completion for.
   * @param item  The item being configured.
   *
   * @return The computed auto-completion candidates.
   */
  @NonNull
  @POST
  public final AutoCompletionCandidates doAutoCompleteRuntimesString(@CheckForNull @QueryParameter String value,
                                                                     @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Runtime.getInstance().autoCompleteIdentifier(value);
  }

  /**
   * Performs validation on a Java charset name.
   *
   * @param value The value to validate.
   * @param item  The item being configured.
   *
   * @return The result of the validation.
   */
  @NonNull
  @POST
  public FormValidation doCheckCharset(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    final String name = Util.fixEmptyAndTrim(value);
    if (name != null) {
      try {
        Charset.forName(value);
      }
      catch (Throwable t) {
        return FormValidation.error(Messages.Command_UnsupportedCharset());
      }
    }
    return FormValidation.ok();
  }

  /**
   * Performs validation on a .NET target framework moniker.
   *
   * @param value The value to validate.
   * @param item  The item being configured.
   *
   * @return The result of the validation.
   */
  @NonNull
  @POST
  public FormValidation doCheckFramework(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Framework.getInstance().checkMoniker(value);
  }

  /**
   * Performs validation on a list of .NET target framework monikers.
   *
   * @param value The value to validate.
   * @param item  The item being configured.
   *
   * @return The result of the validation.
   */
  @NonNull
  @POST
  public FormValidation doCheckFrameworksString(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Framework.getInstance().checkMonikers(value);
  }

  /**
   * Performs validation on a .NET runtime identifier.
   *
   * @param value The value to validate.
   * @param item  The item being configured.
   *
   * @return The result of the validation.
   */
  @NonNull
  @POST
  public FormValidation doCheckRuntime(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Runtime.getInstance().checkIdentifier(value);
  }

  /**
   * Performs validation on a list of .NET runtime identifiers.
   *
   * @param value The values to validate.
   * @param item  The item being configured.
   *
   * @return The result of the validation.
   */
  @NonNull
  @POST
  public FormValidation doCheckRuntimesString(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    return Runtime.getInstance().checkIdentifiers(value);
  }

  /**
   * Fills a listbox with the names of charsets supported by the running version of Java.
   *
   * @param item The item being configured.
   *
   * @return A suitably filled listbox model.
   */
  @NonNull
  @POST
  public final ListBoxModel doFillCharsetItems(@CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.Command_SameCharsetAsBuild(), "");
    for (final Charset cs : Charset.availableCharsets().values()) {
      final Set<String> aliases = cs.aliases();
      final String name = cs.displayName();
      if (aliases == null || aliases.isEmpty()) {
        model.add(name);
      }
      else {
        model.add(String.format("%s (%s)", name, String.join(" / ", aliases)), name);
      }
    }
    return model;
  }

  /**
   * Fills a listbox with the names of .NET SDKs that have been defined as global tools.
   *
   * @param item The item being configured.
   *
   * @return A suitably filled listbox model.
   */
  @NonNull
  @POST
  public final ListBoxModel doFillSdkItems(@CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.Command_DefaultSDK(), "");
    DotNetSDK.addSdks(model);
    return model;
  }

  /**
   * Fills a listbox with the possible values for the .NET CLI "verbosity" option.
   *
   * @param item The item being configured.
   *
   * @return A suitably filled listbox model.
   */
  @NonNull
  @POST
  public final ListBoxModel doFillVerbosityItems(@CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Permission.CONFIGURE);
    }
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
   * Determines whether this descriptor is applicable for the specified job type.
   *
   * @param jobType The job type.
   *
   * @return {@code true}.
   */
  public final boolean isApplicable(@CheckForNull Class<? extends AbstractProject> jobType) {
    if (jobType != null && FreeStyleProject.class.isAssignableFrom(jobType)) {
      final FreeStyleCommandConfiguration configuration = ExtensionList.lookupSingleton(FreeStyleCommandConfiguration.class);
      return this.isApplicableToFreeStyleProjects(configuration);
    }
    return true;
  }

  /**
   * Determines whether this command should be made available to freestyle projects.
   *
   * @param configuration The applicable configuration.
   *
   * @return {@code true} when the command should be available for use in freestyle projects; {@code false} otherwise.
   */
  protected boolean isApplicableToFreeStyleProjects(@NonNull FreeStyleCommandConfiguration configuration) {
    return true;
  }

}
