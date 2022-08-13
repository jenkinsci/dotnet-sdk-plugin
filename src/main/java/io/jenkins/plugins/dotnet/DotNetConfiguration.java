package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.tools.ToolConfigurationCategory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;

/** Global configuration for the .NET SDK plugin. */
@Extension
public class DotNetConfiguration extends GlobalConfiguration implements Serializable {

  private static final long serialVersionUID = 7543404699990461923L;

  /** Creates the global configuration for the .NET SDK plugin. */
  @DataBoundConstructor
  public DotNetConfiguration() {
    super();
    this.load();
  }

  /**
   * Gets the category for this global configuration.
   *
   * @return {@link ToolConfigurationCategory}.
   */
  @NonNull
  @Override
  public GlobalConfigurationCategory getCategory() {
    return GlobalConfigurationCategory.get(ToolConfigurationCategory.class);
  }

  /** Indicates whether the telemetry opt-out is set. */
  private boolean telemetryOptOut = true;

  /**
   * Determines whether the telemetry opt-out is set.
   *
   * @return {@code true} when the telemetry opt-out is set; {@code false} otherwise.
   */
  public boolean isTelemetryOptOut() {
    return this.telemetryOptOut;
  }

  /**
   * Determines whether the telemetry opt-out should be set.
   *
   * @param telemetryOptOut {@code true} to opt out of telemetry; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setTelemetryOptOut(boolean telemetryOptOut) {
    this.telemetryOptOut = telemetryOptOut;
  }

}
