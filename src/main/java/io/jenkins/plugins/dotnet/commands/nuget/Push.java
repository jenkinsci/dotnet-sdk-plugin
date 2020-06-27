package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import hudson.util.VariableResolver;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.CommandDescriptor;
import io.jenkins.plugins.dotnet.commands.Messages;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/** A build step to run "{@code dotnet nuget push}", pushing a package to a server and publishing it. */
public final class Push extends DeleteOrPush {

  /** Creates a new "{@code dotnet nuget push}" build step. */
  @DataBoundConstructor
  public Push() {
  }

  /**
   * Adds command line arguments for this "{@code dotnet nuget push}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code nuget push}</li>
   *   <li>The package file path specified via {@link #setRoot(String)}.</li>
   *   <li>
   *     Any arguments added by {@link DeleteOrPush#addCommandLineArguments(Run, ArgumentListBuilder, VariableResolver, Set)}.
   *   </li>
   *   <li>{@code --disable-buffering}, if requested via {@link #setDisableBuffering(boolean)}.</li>
   *   <li>{@code --no-symbols}, if requested via {@link #setNoSymbols(boolean)}.</li>
   *   <li>{@code --skip-duplicate}, if requested via {@link #setSkipDuplicate(boolean)}.</li>
   *   <li>{@code --symbol-api-key xxx}, if an API key was specified via {@link #setSymbolApiKeyId(String)}.</li>
   *   <li>{@code --symbol-source xxx}, if a symbol source was specified via {@link #setSymbolSource(String)}.</li>
   *   <li>{@code --timeout nnn}, if a timeout was specified via {@link #setTimeout(Integer)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) throws AbortException {
    args.add("nuget", "push", this.root);
    super.addCommandLineArguments(run, args, resolver, sensitive);
    if (this.disableBuffering)
      args.add("--disable-buffering");
    if (this.noSymbols)
      args.add("--no-symbols");
    if (this.skipDuplicate)
      args.add("--skip-duplicate");
    NuGetCommand.addApiKeyOption(run, args, "--symbol-api-key", this.symbolApiKeyId);
    if (this.symbolSource != null)
      args.add("--symbol-source", this.symbolSource);
    if (this.timeout != null)
      args.add("--timeout", Integer.toString(this.timeout));
  }

  //region Properties

  private boolean disableBuffering;

  public boolean isDisableBuffering() {
    return this.disableBuffering;
  }

  @DataBoundSetter
  public void setDisableBuffering(boolean disableBuffering) {
    this.disableBuffering = disableBuffering;
  }

  private boolean noSymbols;

  public boolean isNoSymbols() {
    return this.noSymbols;
  }

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

  public boolean isSkipDuplicate() {
    return this.skipDuplicate;
  }

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

  public String getSymbolSource() {
    return this.symbolSource;
  }

  @DataBoundSetter
  public void setSymbolSource(String symbolSource) {
    this.symbolSource = Util.fixEmptyAndTrim(symbolSource);
  }

  private Integer timeout;

  public Integer getTimeout() {
    return this.timeout;
  }

  @DataBoundSetter
  public void setTimeout(Integer timeout) {
    if (timeout != null && timeout <= 0)
      timeout = null;
    this.timeout = timeout;
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
      return Messages.NuGet_Push_DisplayName();
    }

  }

  //endregion

}
