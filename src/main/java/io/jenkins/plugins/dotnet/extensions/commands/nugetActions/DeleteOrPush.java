package io.jenkins.plugins.dotnet.extensions.commands.nugetActions;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Util;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.DotNetUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.verb.POST;

public abstract class DeleteOrPush extends NuGetAction {

  private static final long serialVersionUID = -2472979986897284361L;

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    super.addCommandLineArguments(arguments);
    arguments.addStringCredential("api-key", this.apiKeyId);
    arguments.addFlag("no-service-endpoint", this.noServiceEndpoint);
    arguments.addOption("source", this.source);
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

  /**
   * Indicates whether the service endpoint ({@code api/v2/package}) should be added to the configured package source URL.
   *
   * @return {@code true} if the package source URL will be used as-is; {@code false} if {@code api/v2/package} will be appended to
   * it.
   */
  public boolean isNoServiceEndpoint() {
    return this.noServiceEndpoint;
  }

  /**
   * Sets whether the service endpoint ({@code api/v2/package}) should be added to the configured package source URL.
   *
   * @param noServiceEndpoint {@code true} if the package source URL should be used as-is; {@code false} if {@code api/v2/package}
   *                          should be appended to * it.
   */
  @DataBoundSetter
  public void setNoServiceEndpoint(boolean noServiceEndpoint) {
    this.noServiceEndpoint = noServiceEndpoint;
  }

  private String source;

  /**
   * Sets the package source to use.
   *
   * @return The package source to use.
   */
  public String getSource() {
    return this.source;
  }

  /**
   * Sets the package source to use.
   *
   * @param source The package source to use.
   */
  @DataBoundSetter
  public void setSource(String source) {
    this.source = Util.fixEmptyAndTrim(source);
  }

  //endregion

  public interface StaplerMethods {

    /**
     * Fills a listbox with all possible API keys (string credentials) defined in the system.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    default ListBoxModel doFillApiKeyIdItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      return DotNetUtils.getStringCredentialsList(true);
    }

  }

}
