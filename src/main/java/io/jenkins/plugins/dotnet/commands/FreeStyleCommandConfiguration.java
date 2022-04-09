package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.tools.ToolConfigurationCategory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

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

}
