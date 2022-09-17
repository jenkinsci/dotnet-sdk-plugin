package io.jenkins.plugins.dotnet.extensions.commands.listWhat;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class References extends ListWhat {

  private static final long serialVersionUID = 3291344502893115786L;

  @DataBoundConstructor
  public References() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet list package}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>{@code reference}</li>
   * </ol>
   */
  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption(this.project);
    arguments.addOption("reference");
  }

  //region Properties

  private String project;

  /**
   * Gets the project to list project references for.
   *
   * @return The project to list project references for.
   */
  @CheckForNull
  public String getProject() {
    return this.project;
  }

  /**
   * Sets the project to list project references for.
   *
   * @param project The project to list project references for.
   */
  @DataBoundSetter
  public void setProject(@CheckForNull String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  //endregion

  @Extension
  @Symbol("references")
  public static final class DescriptorImpl extends ListWhatDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.List_Reference_DisplayName();
    }

  }

}
