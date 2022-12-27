package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import io.jenkins.plugins.dotnet.extensions.commands.nugetActions.Locals.SpecialValues;
import io.jenkins.plugins.dotnet.extensions.commands.nugetActions.Locals.StaplerMethods;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** A build step to run "{@code dotnet nuget locals}", listing or clearing one or more local package stores. */
public final class Locals extends NuGetCommand {

  /** Creates a new "{@code dotnet nuget locals}" build step. */
  @DataBoundConstructor
  public Locals() {
    super("locals");
    this.cacheLocation = SpecialValues.defaultLocation();
    this.operation = SpecialValues.defaultOperation();
  }

  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    super.addCommandLineArguments(args);
    args.add(this.cacheLocation);
    args.addFlag(this.operation, true);
  }

  //region Properties

  @NonNull
  private String cacheLocation;

  @NonNull
  public String getCacheLocation() {
    return this.cacheLocation;
  }

  @DataBoundSetter
  public void setCacheLocation(@NonNull String cacheLocation) {
    this.cacheLocation = SpecialValues.checkCacheLocation(cacheLocation);
  }

  @NonNull
  private String operation;

  @NonNull
  public String getOperation() {
    return this.operation;
  }

  @DataBoundSetter
  public void setOperation(@NonNull String operation) {
    this.operation = SpecialValues.checkOperation(operation);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet nuget locals}" build steps. */
  @Extension
  @Symbol("dotnetNuGetLocals")
  public static final class DescriptorImpl extends NuGetCommandDescriptor implements StaplerMethods {

    /** Creates a new "{@code dotnet nuget locals}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.NuGet_Locals_DisplayName();
    }

  }

  //endregion

}
