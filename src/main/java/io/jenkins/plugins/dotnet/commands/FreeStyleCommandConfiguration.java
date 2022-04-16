package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.commands.msbuild.Build;
import io.jenkins.plugins.dotnet.commands.msbuild.Clean;
import io.jenkins.plugins.dotnet.commands.msbuild.Pack;
import io.jenkins.plugins.dotnet.commands.msbuild.Publish;
import io.jenkins.plugins.dotnet.commands.msbuild.Test;
import io.jenkins.plugins.dotnet.commands.nuget.Delete;
import io.jenkins.plugins.dotnet.commands.nuget.Locals;
import io.jenkins.plugins.dotnet.commands.nuget.Push;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.model.Jenkins;
import jenkins.tools.ToolConfigurationCategory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.io.Serializable;

@Extension
public class FreeStyleCommandConfiguration extends GlobalConfiguration implements Serializable {

  private static final long serialVersionUID = 7543404699990461923L;

  @DataBoundConstructor
  public FreeStyleCommandConfiguration() {
    super();
    this.load();
  }

  @NonNull
  @Override
  public GlobalConfigurationCategory getCategory() {
    return GlobalConfigurationCategory.get(ToolConfigurationCategory.class);
  }

  @Override
  public String getConfigPage() {
    return super.getConfigPage();
  }

  private boolean buildAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckBuildAllowed(@QueryParameter boolean buildAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(buildAllowed, Build.class);
  }

  /**
   * Determines whether the "build" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isBuildAllowed() {
    return this.buildAllowed;
  }

  /**
   * Determines whether the "build" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setBuildAllowed(boolean allowed) {
    this.buildAllowed = allowed;
    this.save();
  }

  private boolean cleanAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckCleanAllowed(@QueryParameter boolean cleanAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(cleanAllowed, Clean.class);
  }

  /**
   * Determines whether the "clean" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isCleanAllowed() {
    return this.cleanAllowed;
  }

  /**
   * Determines whether the "clean" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setCleanAllowed(boolean allowed) {
    this.cleanAllowed = allowed;
    this.save();
  }

  private boolean listPackageAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckListPackageAllowed(@QueryParameter boolean listPackageAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(listPackageAllowed, ListPackage.class);
  }

  /**
   * Determines whether the "list package" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isListPackageAllowed() {
    return this.listPackageAllowed;
  }

  /**
   * Determines whether the "list package" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setListPackageAllowed(boolean allowed) {
    this.listPackageAllowed = allowed;
    this.save();
  }

  private boolean nuGetDeleteAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckNuGetDeleteAllowed(@QueryParameter boolean nuGetDeleteAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(nuGetDeleteAllowed, Delete.class);
  }

  /**
   * Determines whether the "nuget delete" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isNuGetDeleteAllowed() {
    return this.nuGetDeleteAllowed;
  }

  /**
   * Determines whether the "nuget delete" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNuGetDeleteAllowed(boolean allowed) {
    this.nuGetDeleteAllowed = allowed;
    this.save();
  }

  private boolean nuGetLocalsAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckNuGetLocalsAllowed(@QueryParameter boolean nuGetLocalsAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(nuGetLocalsAllowed, Locals.class);
  }

  /**
   * Determines whether the "nuget locals" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isNuGetLocalsAllowed() {
    return this.nuGetLocalsAllowed;
  }

  /**
   * Determines whether the "nuget locals" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNuGetLocalsAllowed(boolean allowed) {
    this.nuGetLocalsAllowed = allowed;
    this.save();
  }

  private boolean nuGetPushAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckNuGetPushAllowed(@QueryParameter boolean nuGetPushAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(nuGetPushAllowed, Push.class);
  }

  /**
   * Determines whether the "nuget push" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isNuGetPushAllowed() {
    return this.nuGetPushAllowed;
  }

  /**
   * Determines whether the "nuget push" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNuGetPushAllowed(boolean allowed) {
    this.nuGetPushAllowed = allowed;
    this.save();
  }

  private boolean packAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckPackAllowed(@QueryParameter boolean packAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(packAllowed, Pack.class);
  }

  /**
   * Determines whether the "pack" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isPackAllowed() {
    return this.packAllowed;
  }

  /**
   * Determines whether the "pack" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setPackAllowed(boolean allowed) {
    this.packAllowed = allowed;
    this.save();
  }

  private boolean publishAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckPublishAllowed(@QueryParameter boolean publishAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(publishAllowed, Publish.class);
  }

  /**
   * Determines whether the "publish" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isPublishAllowed() {
    return this.publishAllowed;
  }

  /**
   * Determines whether the "publish" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setPublishAllowed(boolean allowed) {
    this.publishAllowed = allowed;
    this.save();
  }

  private boolean restoreAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckRestoreAllowed(@QueryParameter boolean restoreAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(restoreAllowed, Restore.class);
  }

  /**
   * Determines whether the "restore" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isRestoreAllowed() {
    return this.restoreAllowed;
  }

  /**
   * Determines whether the "restore" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setRestoreAllowed(boolean allowed) {
    this.restoreAllowed = allowed;
    this.save();
  }

  private boolean testAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckTestAllowed(@QueryParameter boolean testAllowed) {
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(testAllowed, Test.class);
  }

  /**
   * Determines whether the "test" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isTestAllowed() {
    return this.testAllowed;
  }

  /**
   * Determines whether the "test" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setTestAllowed(boolean allowed) {
    this.testAllowed = allowed;
    this.save();
  }

  private boolean toolRestoreAllowed = true;

  @NonNull
  @POST
  public FormValidation doCheckToolRestoreAllowed(@QueryParameter boolean toolRestoreAllowed) {
    final Class<? extends Command> toolRestore = io.jenkins.plugins.dotnet.commands.tool.Restore.class;
    return FreeStyleCommandConfiguration.ensureNotInUseWhenDisallowed(toolRestoreAllowed, toolRestore);
  }

  /**
   * Determines whether the "tool restore" command should be available for use in freestyle projects.
   *
   * @return {@code true} if the command should be available; {@code false} otherwise.
   */
  public boolean isToolRestoreAllowed() {
    return this.toolRestoreAllowed;
  }

  /**
   * Determines whether the "tool restore" command should be available for use in freestyle projects.
   *
   * @param allowed {@code true} if the command should be available; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setToolRestoreAllowed(boolean allowed) {
    this.toolRestoreAllowed = allowed;
    this.save();
  }

  @NonNull
  private static FormValidation ensureNotInUseWhenDisallowed(boolean allowed, @NonNull Class<? extends Command> command) {
    if (allowed) {
      return FormValidation.ok();
    }
    final int uses = Jenkins.get().getAllItems(FreeStyleProject.class,
      project -> FreeStyleCommandConfiguration.includesCommand(project, command)).size();
    if (uses == 0) {
      return FormValidation.ok();
    }
    return FormValidation.warning(Messages.FreeStyleCommandConfiguration_StillInUse(uses));
  }

  private static boolean includesCommand(@NonNull FreeStyleProject project, @NonNull Class<? extends Command> command) {
    for (final Builder step: project.getBuilders()) {
      if (step.getClass() == command) {
        return true;
      }
    }
    return false;
  }

}
