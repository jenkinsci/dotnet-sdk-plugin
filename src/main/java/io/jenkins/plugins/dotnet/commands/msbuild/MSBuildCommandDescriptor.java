package io.jenkins.plugins.dotnet.commands.msbuild;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.commands.CommandDescriptor;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.kohsuke.stapler.QueryParameter;

import java.io.StringReader;
import java.util.Properties;

/** A descriptor for an MSBuild-based .NET command. */
public abstract class MSBuildCommandDescriptor extends CommandDescriptor {

  /**
   * Creates a new .NET MSBuild command descriptor instance.
   * <p>
   * This version works when you follow the common convention, where a descriptor is written as the static nested class of the
   * describable class.
   */
  protected MSBuildCommandDescriptor() {
  }

  /**
   * Creates a new .NET MSBuild command descriptor instance for a specific class.
   *
   * @param clazz The class implementing the command described by this descriptor instance.
   */
  protected MSBuildCommandDescriptor(@NonNull Class<? extends MSBuildCommand> clazz) {
    super(clazz);
  }

  /**
   * Performs validation on a set of MSBuild properties.
   *
   * @param value The value to validate.
   *
   * @return The result of the validation.
   */
  @SuppressWarnings("unused")
  @NonNull
  public FormValidation doCheckProperties(@QueryParameter String value) {
    try {
      new Properties().load(new StringReader(value));
    }
    catch (Throwable t) {
      return FormValidation.error(t, Messages.MSBuild_Command_InvalidProperties());
    }
    return FormValidation.ok();
  }

  /**
   * Fills a combobox with standard MSBuild configuration names.
   *
   * @return A suitable filled combobox model.
   */
  @SuppressWarnings("unused")
  public final ComboBoxModel doFillConfigurationItems() {
    final ComboBoxModel model = new ComboBoxModel();
    // Note: not localized
    model.add("Debug");
    model.add("Release");
    return model;
  }

}
