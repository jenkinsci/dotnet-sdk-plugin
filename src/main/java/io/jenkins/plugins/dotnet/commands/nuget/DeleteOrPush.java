package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Util;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import org.kohsuke.stapler.DataBoundSetter;

public class DeleteOrPush extends NuGetCommand {

  public DeleteOrPush() {
  }

  public DeleteOrPush(@NonNull String subCommand) {
    super(subCommand);
  }

  /**
   * Adds command line arguments for this .NET NuGet {@code delete} or {@code push} command invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link NuGetCommand#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>{@code --api-key xxx}, if an API key was specified via {@link #setApiKeyId(String)}.</li>
   *   <li>{@code --no-service-endpoint}, if requested via {@link #setNoServiceEndpoint(boolean)}.</li>
   *   <li>{@code --source xxx}, if a source was specified via {@link #setSource(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    super.addCommandLineArguments(args);
    args.addStringCredential("api-key", this.apiKeyId);
    args.addFlag("no-service-endpoint", this.noServiceEndpoint);
    args.addOption("source", this.source);
  }

  //region Properties

  private String apiKeyId;

  /**
   * Gets the package server API key to use.
   *
   * @return The package server API key to use.
   */
  @CheckForNull
  public String getApiKeyId() {
    return this.apiKeyId;
  }

  /**
   * Sets the package server API key to use.
   *
   * @param apiKeyId The package server API key to use.
   */
  @DataBoundSetter
  public void setApiKeyId(@CheckForNull String apiKeyId) {
    this.apiKeyId = Util.fixEmptyAndTrim(apiKeyId);
  }

  private boolean noServiceEndpoint;

  public boolean isNoServiceEndpoint() {
    return this.noServiceEndpoint;
  }

  @DataBoundSetter
  public void setNoServiceEndpoint(boolean noServiceEndpoint) {
    this.noServiceEndpoint = noServiceEndpoint;
  }

  private String source;

  public String getSource() {
    return this.source;
  }

  @DataBoundSetter
  public void setSource(String source) {
    this.source = Util.fixEmptyAndTrim(source);
  }

  //endregion

}
