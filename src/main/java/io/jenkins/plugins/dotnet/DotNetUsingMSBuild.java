package io.jenkins.plugins.dotnet;

import org.kohsuke.stapler.DataBoundSetter;

public abstract class DotNetUsingMSBuild extends DotNet {

  //region Properties

  public boolean isShutDownBuildServers() {
    return this.shutDownBuildServers;
  }

  @DataBoundSetter
  public void setShutDownBuildServers(boolean shutDownBuildServers) {
    this.shutDownBuildServers = shutDownBuildServers;
  }

  public boolean isUnstableIfWarnings() {
    return this.unstableIfWarnings;
  }

  @DataBoundSetter
  public void setUnstableIfWarnings(boolean unstableIfWarnings) {
    this.unstableIfWarnings = unstableIfWarnings;
  }

  //endregion

}
