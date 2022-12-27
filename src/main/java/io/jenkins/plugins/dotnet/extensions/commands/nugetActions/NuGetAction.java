package io.jenkins.plugins.dotnet.extensions.commands.nugetActions;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.CommandLineArgumentProvider;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;

public abstract class NuGetAction extends AbstractDescribableImpl<NuGetAction>
  implements CommandLineArgumentProvider, ExtensionPoint, Serializable {

  private static final long serialVersionUID = 6930053378148054112L;

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addFlag("force-english-output", this.forceEnglishOutput);
  }

  //region Properties

  /** Indicates whether the command output should be forced to be in English. */
  private boolean forceEnglishOutput;

  /**
   * Determines whether the command output should be forced to be in English.
   *
   * @return {@code true} when the command output should be forced to be in English; {@code false} otherwise.
   */
  public boolean isForceEnglishOutput() {
    return this.forceEnglishOutput;
  }

  /**
   * Determines whether the command output should be forced to be in English.
   *
   * @param forceEnglishOutput {@code true} to force the command output to be in English; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setForceEnglishOutput(boolean forceEnglishOutput) {
    this.forceEnglishOutput = forceEnglishOutput;
  }

  //endregion

}
