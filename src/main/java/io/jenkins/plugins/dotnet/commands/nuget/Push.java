package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetConfiguration;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.verb.POST;

/** A build step to run "{@code dotnet nuget push}", pushing a package to a server and publishing it. */
public final class Push extends DeleteOrPush {

  /** Creates a new "{@code dotnet nuget push}" build step. */
  @DataBoundConstructor
  public Push() {
    super("push");
  }

  /**
   * Adds command line arguments for this "{@code dotnet nuget push}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link DeleteOrPush#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>The package file path specified via {@link #setRoot(String)}.</li>
   *   <li>{@code --disable-buffering}, if requested via {@link #setDisableBuffering(boolean)}.</li>
   *   <li>{@code --no-symbols}, if requested via {@link #setNoSymbols(boolean)}.</li>
   *   <li>{@code --skip-duplicate}, if requested via {@link #setSkipDuplicate(boolean)}.</li>
   *   <li>{@code --symbol-api-key xxx}, if an API key was specified via {@link #setSymbolApiKeyId(String)}.</li>
   *   <li>{@code --symbol-source xxx}, if a symbol source was specified via {@link #setSymbolSource(String)}.</li>
   *   <li>{@code --timeout nnn}, if a timeout was specified via {@link #setTimeout(Integer)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    super.addCommandLineArguments(args);
    args.addOption(this.root);
    args.addFlag("disable-buffering", this.disableBuffering);
    args.addFlag("no-symbols", this.noSymbols);
    args.addFlag("skip-duplicate", this.skipDuplicate);
    args.addStringCredential("symbol-api-key", this.symbolApiKeyId);
    args.addOption("symbol-source", this.symbolSource);
    args.addOption("timeout", this.timeout);
  }

  //region Properties

  private boolean disableBuffering;

  /**
   * Indicates whether buffering should be disabled when pushing to an HTTP(S) source.
   *
   * @return {@code true} when buffering should be disabled when pushing to an HTTP(S) source; {@code false} otherwise.
   */
  public boolean isDisableBuffering() {
    return this.disableBuffering;
  }

  /**
   * Determine whether buffering should be disabled when pushing to an HTTP(S) source.
   *
   * @param disableBuffering {@code true} to disable buffering when pushing to an HTTP(S) source; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setDisableBuffering(boolean disableBuffering) {
    this.disableBuffering = disableBuffering;
  }

  private boolean noSymbols;

  /**
   * Indicates whether symbols will not get pushed even when present.
   *
   * @return {@code true} when symbols should not get pushed, even when present; {@code false} otherwise.
   */
  public boolean isNoSymbols() {
    return this.noSymbols;
  }

  /**
   * Determine whether symbols will not get pushed even when present.
   *
   * @param noSymbols {@code true} not to push symbols, even when present; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoSymbols(boolean noSymbols) {
    this.noSymbols = noSymbols;
  }

  private String root;

  /**
   * Gets the project to push packages for.
   *
   * @return The project to push packages for.
   */
  @CheckForNull
  public String getRoot() {
    return this.root;
  }

  /**
   * Sets the project to push packages for.
   *
   * @param root The project to push packages for.
   */
  @DataBoundSetter
  public void setRoot(@CheckForNull String root) {
    this.root = Util.fixEmptyAndTrim(root);
  }

  private boolean skipDuplicate;

  /**
   * Indicates whether duplicates (409 Conflict responses) should be treated as warnings when pushing multiple packages to an
   * HTTP(S) source, allowing the push to continue.
   *
   * @return {@code true} when duplicates should be treated as warnings when pushing multiple packages; {@code false} otherwise.
   */
  public boolean isSkipDuplicate() {
    return this.skipDuplicate;
  }

  /**
   * Determine whether duplicates (409 Conflict responses) should be treated as warnings when pushing multiple packages to an
   * HTTP(S) source, allowing the push to continue.
   *
   * @param skipDuplicate {@code true} to treat duplicates as warnings when pushing multiple packages; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setSkipDuplicate(boolean skipDuplicate) {
    this.skipDuplicate = skipDuplicate;
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

  private String symbolSource;

  /**
   * Gets the symbol server URL.
   *
   * @return The symbol server URL.
   */
  public String getSymbolSource() {
    return this.symbolSource;
  }

  /**
   * Sets the symbol server URL.
   *
   * @param symbolSource The symbol server URL.
   */
  @DataBoundSetter
  public void setSymbolSource(String symbolSource) {
    this.symbolSource = Util.fixEmptyAndTrim(symbolSource);
  }

  private Integer timeout;

  /**
   * Gets the timeout for pushing to a server, in seconds.
   *
   * @return The timeout for pushing to a server, in seconds.
   */
  public Integer getTimeout() {
    return this.timeout;
  }

  /**
   * Sets the timeout for pushing to a server, in seconds.
   *
   * @param timeout The timeout for pushing to a server, in seconds.
   */
  @DataBoundSetter
  public void setTimeout(@CheckForNull Integer timeout) {
    if (timeout != null && timeout <= 0) {
      timeout = null;
    }
    this.timeout = timeout;
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet nuget push}" build steps. */
  @Extension
  @Symbol("dotnetNuGetPush")
  public static final class DescriptorImpl extends NuGetCommandDescriptor {

    /** Creates a new "{@code dotnet nuget push}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Fills a listbox with all possible API keys (string credentials) defined in the system.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    public ListBoxModel doFillApiKeyIdItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      return DotNetUtils.getStringCredentialsList(true);
    }

    /**
     * Fills a listbox with all possible API keys (string credentials) defined in the system.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    public ListBoxModel doFillSymbolApiKeyIdItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      return DotNetUtils.getStringCredentialsList(true);
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.NuGet_Push_DisplayName();
    }

    @Override
    protected boolean isApplicableToFreeStyleProjects(@NonNull DotNetConfiguration configuration) {
      return configuration.isNuGetPushAllowed();
    }

  }

  //endregion

}
