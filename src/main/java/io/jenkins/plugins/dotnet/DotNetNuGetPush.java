package io.jenkins.plugins.dotnet;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import hudson.util.VariableResolver;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/** A build step to run "{@code dotnet nuget push}", pushing a package to a server and publishing it. */
public final class DotNetNuGetPush extends DotNet {

  /** Creates a new "{@code dotnet nuget push}" build step. */
  @DataBoundConstructor
  public DotNetNuGetPush() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet nuget push}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code nuget push}</li>
   *   <li>The project specified via {@link #setProject(String)}.</li>
   *   <li>{@code --api-key xxx}, if an API key was specified via {@link #setApiKeyId(String)}</li>
   *   <li>{@code --symbol-api-key xxx}, if an API key was specified via {@link #setSymbolApiKeyId(String)}</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) throws AbortException {
    args.add("nuget", "push");
    args.add(this.project);
    if (this.apiKeyId != null) {
      final StringCredentials credential = CredentialsProvider.findCredentialById(this.apiKeyId, StringCredentials.class, run);
      if (credential == null)
        throw new AbortException(Messages.DotNetNuGetPush_NoCredentialsFound(this.apiKeyId));
      args.add("--api-key").add(credential.getSecret(), true);
    }
    if (this.symbolApiKeyId != null) {
      final StringCredentials credential = CredentialsProvider.findCredentialById(this.symbolApiKeyId, StringCredentials.class, run);
      if (credential == null)
        throw new AbortException(Messages.DotNetNuGetPush_NoCredentialsFound(this.symbolApiKeyId));
      args.add("--symbol-api-key").add(credential.getSecret(), true);
    }
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

  private String project;

  /**
   * Gets the project to push packages for.
   *
   * @return The project to push packages for.
   */
  @CheckForNull
  public String getProject() {
    return this.project;
  }

  /**
   * Sets the project to push packages for.
   *
   * @param project The project to push packages for.
   */
  @DataBoundSetter
  public void setProject(@CheckForNull String project) {
    this.project = Util.fixEmptyAndTrim(project);
  }

  private String symbolApiKeyId;

  /**
   * Gets the symbol server API key to use.
   *
   * @return The symbol server API key to use.
   */
  @CheckForNull
  public String getSymbolApiKeyId() {
    return this.symbolApiKeyId;
  }

  /**
   * Sets the symbol server API key to use.
   *
   * @param symbolApiKeyId The symbol server API key to use.
   */
  @DataBoundSetter
  public void setSymbolApiKeyId(@CheckForNull String symbolApiKeyId) {
    this.symbolApiKeyId = Util.fixEmptyAndTrim(symbolApiKeyId);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet nuget push}" build steps. */
  @Extension
  @Symbol("dotnetNuGetPush")
  public static final class DescriptorImpl extends CommandDescriptor {

    /** Creates a new "{@code dotnet nuget push}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillApiKeyIdItems(@CheckForNull @AncestorInPath Jenkins context) {
      return DotNetUtils.getStringCredentialsList(context, true);
    }

    @SuppressWarnings("unused")
    @NonNull
    public ListBoxModel doFillSymbolApiKeyIdItems(@CheckForNull @AncestorInPath Jenkins context) {
      return DotNetUtils.getStringCredentialsList(context, true);
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.DotNetNuGetPush_DisplayName();
    }

  }

  //endregion

}
