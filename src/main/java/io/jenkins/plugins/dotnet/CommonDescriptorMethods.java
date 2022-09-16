package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.nio.charset.Charset;
import java.util.Set;

/*** Methods common to the several of the descriptors in this plugin. */
public interface CommonDescriptorMethods {

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
  default AutoCompletionCandidates doAutoCompleteFramework(@CheckForNull @QueryParameter String value,
                                                           @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default AutoCompletionCandidates doAutoCompleteFrameworksString(@CheckForNull @QueryParameter String value,
                                                                  @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default AutoCompletionCandidates doAutoCompleteRuntime(@CheckForNull @QueryParameter String value,
                                                         @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default AutoCompletionCandidates doAutoCompleteRuntimesString(@CheckForNull @QueryParameter String value,
                                                                @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default FormValidation doCheckCharset(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
    }
    final String name = Util.fixEmptyAndTrim(value);
    if (name != null) {
      try {
        Charset.forName(value);
      }
      catch (Throwable t) {
        return FormValidation.error(Messages.DotNetBuildStep_UnsupportedCharset());
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
  default FormValidation doCheckFramework(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default FormValidation doCheckFrameworksString(@CheckForNull @QueryParameter String value,
                                                 @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default FormValidation doCheckRuntime(@CheckForNull @QueryParameter String value, @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default FormValidation doCheckRuntimesString(@CheckForNull @QueryParameter String value,
                                               @CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
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
  default ListBoxModel doFillCharsetItems(@CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
    }
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.DotNetBuildStep_SameCharsetAsBuild(), "");
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
  default ListBoxModel doFillSdkItems(@CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
    }
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.DotNetBuildStep_DefaultSDK(), "");
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
  default ListBoxModel doFillVerbosityItems(@CheckForNull @AncestorInPath Item item) {
    if (item != null) {
      item.checkPermission(Item.CONFIGURE);
    }
    final ListBoxModel model = new ListBoxModel();
    model.add(Messages.DotNetBuildStep_Verbosity_Default(), "");
    model.add(Messages.DotNetBuildStep_Verbosity_Quiet(), "q");
    model.add(Messages.DotNetBuildStep_Verbosity_Minimal(), "m");
    model.add(Messages.DotNetBuildStep_Verbosity_Normal(), "n");
    model.add(Messages.DotNetBuildStep_Verbosity_Detailed(), "d");
    model.add(Messages.DotNetBuildStep_Verbosity_Diagnostic(), "diag");
    return model;
  }

  /**
   * Gets the button text to use for the "Advanced" button.
   *
   * @return "More Options", or the localized equivalent.
   */
  @SuppressWarnings("unused")
  @NonNull
  default String getMoreOptions() {
    return Messages.DotNetBuildStep_MoreOptions();
  }

}
