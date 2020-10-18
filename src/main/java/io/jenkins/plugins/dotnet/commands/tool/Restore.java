package io.jenkins.plugins.dotnet.commands.tool;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.structs.describable.UninstantiatedDescribable;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.HashMap;
import java.util.Map;

/** A build step to run "{@code dotnet tool restore}", restoring local tools as described in a tool manifest. */
public final class Restore extends ToolCommand {

  /** Creates a new "{@code dotnet tool restore}" build step. */
  @DataBoundConstructor
  public Restore() {
    super("restore");
  }

  /**
   * Adds command line arguments for this "{@code dotnet nuget push}" invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>Any arguments added by {@link ToolCommand#addCommandLineArguments(DotNetArguments)}.</li>
   *   <li>{@code --add-source xxx}, for each source specified via {@link #setAdditionalSourcesString(String)}.</li>
   *   <li>{@code --configfile xxx}, if a config file was specified via {@link #setConfigfile(String)}.</li>
   *   <li>{@code --disable-parallel}, if requested via {@link #setDisableParallel(boolean)}.</li>
   *   <li>{@code --ignore-failed-sources}, if requested via {@link #setIgnoreFailedSources(boolean)}.</li>
   *   <li>{@code --no-cache}, if requested via {@link #setNoCache(boolean)}.</li>
   *   <li>{@code --tool-manifest xxx}, if a tool manifest was specified via {@link #setToolManifest(String)}.</li>
   *   <li>{@code -v:xxx}, if a verbosity has been specified via {@link #setVerbosity(String)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    super.addCommandLineArguments(args);
    args.addOptions("add-source", this.additionalSources);
    args.addOption("configfile", this.configfile);
    args.addFlag("disable-parallel", this.disableParallel);
    args.addFlag("ignore-failed-sources", this.ignoreFailedSources);
    args.addFlag("no-cache", this.noCache);
    args.addOption("tool-manifest", this.toolManifest);
    args.addOption('v', this.verbosity);
  }

  //region Properties

  private String additionalSources;

  /**
   * Gets the sole additional source to use for the restore.
   *
   * @return The sole additional source to use for the restore, or {@code null} when there is not exactly one additional source set.
   */
  public String getAdditionalSource() {
    if (this.additionalSources == null)
      return null;
    final String[] additionalSources = Util.tokenize(this.additionalSources);
    if (additionalSources.length != 1)
      return null;
    return additionalSources[0];
  }

  /**
   * Sets the sole additional source to use for the restore.
   *
   * @param additionalSource The sole additional source to use for the restore.
   */
  @DataBoundSetter
  public void setAdditionalSource(String additionalSource) {
    this.additionalSources = Util.fixEmptyAndTrim(additionalSource);
  }

  /**
   * Gets the list of additional sources to use for the restore.
   *
   * @return The list of additional sources to use for the restore.
   */
  public String[] getAdditionalSources() {
    if (this.additionalSources == null)
      return null;
    return Util.tokenize(this.additionalSources);
  }

  /**
   * Sets the list of additional sources to use for the restore.
   *
   * @param additionalSources The list of additional sources to use for the restore.
   */
  @DataBoundSetter
  public void setAdditionalSources(String... additionalSources) {
    this.additionalSources = DotNetUtils.detokenize(additionalSources, ' ');
  }

  /**
   * Gets the list of additional sources to use for the restore.
   *
   * @return The list of additional sources to use for the restore.
   */
  public String getAdditionalSourcesString() {
    return this.additionalSources;
  }

  /**
   * Sets the list of additional sources to use for the restore.
   *
   * @param additionalSources The list of additional sources to use for the restore.
   */
  @DataBoundSetter
  public void setAdditionalSourcesString(String additionalSources) {
    this.additionalSources = Util.fixEmptyAndTrim(additionalSources);
  }

  private String configfile;

  /**
   * Gets the NuGet configuration (nuget.config) file to use.
   *
   * @return The NuGet configuration (nuget.config) file to use.
   */
  public String getConfigfile() {
    return this.configfile;
  }

  /**
   * Sets the NuGet configuration (nuget.config) file to use.
   *
   * @param configfile The NuGet configuration (nuget.config) file to use.
   */
  @DataBoundSetter
  public void setConfigfile(String configfile) {
    this.configfile = Util.fixEmptyAndTrim(configfile);
  }

  private boolean disableParallel;

  /**
   * Determines whether or not multiple projects can be restored in parallel.
   *
   * @return {@code true} when multiple projects are restored one by one; {@code false} otherwise.
   */
  public boolean isDisableParallel() {
    return this.disableParallel;
  }

  /**
   * Determines whether or not multiple projects can be restored in parallel.
   *
   * @param disableParallel {@code true} to restore multiple projects one by one; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setDisableParallel(boolean disableParallel) {
    this.disableParallel = disableParallel;
  }

  private boolean ignoreFailedSources;

  /**
   * Determines whether or not failed sources should be ignored.
   *
   * @return {@code true} when failed sources are ignored; {@code false} otherwise.
   */
  public boolean isIgnoreFailedSources() {
    return this.ignoreFailedSources;
  }

  /**
   * Determines whether or not failed sources should be ignored.
   *
   * @param ignoreFailedSources {@code true} to ignore failed sources; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setIgnoreFailedSources(boolean ignoreFailedSources) {
    this.ignoreFailedSources = ignoreFailedSources;
  }

  private boolean noCache;

  /**
   * Determines whether or not HTTP requests should be cached.
   *
   * @return {@code true} when HTTP requests are not cached; {@code false} otherwise.
   */
  public boolean isNoCache() {
    return this.noCache;
  }

  /**
   * Determines whether or not HTTP requests should be cached.
   *
   * @param noCache {@code true} not to cache HTTP requests; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  private String toolManifest;

  /**
   * Gets the path to the manifest file.
   *
   * @return The path to the manifest file.
   */
  public String getToolManifest() {
    return this.toolManifest;
  }

  /**
   * Sets the path to the manifest file.
   *
   * @param toolManifest The path to the manifest file.
   */
  @DataBoundSetter
  public void setToolManifest(String toolManifest) {
    this.toolManifest = Util.fixEmptyAndTrim(toolManifest);
  }

  private String verbosity;

  /**
   * Gets the verbosity to use for the command.
   *
   * @return The verbosity to use for the command.
   */
  @CheckForNull
  public String getVerbosity() {
    return this.verbosity;
  }

  /**
   * Sets the verbosity to use for the command.
   *
   * @param verbosity The verbosity to use for the command.
   */
  @DataBoundSetter
  public void setVerbosity(@CheckForNull String verbosity) {
    this.verbosity = Util.fixEmptyAndTrim(verbosity);
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet tool restore}" build steps. */
  @Extension
  @Symbol("dotnetToolRestore")
  public static final class DescriptorImpl extends ToolCommandDescriptor {

    /** Creates a new "{@code dotnet tool restore}" build step descriptor instance. */
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
      return Messages.Tool_Restore_DisplayName();
    }

    @NonNull
    @Override
    public UninstantiatedDescribable customUninstantiate(@NonNull UninstantiatedDescribable ud) {
      ud = super.customUninstantiate(ud);
      final Map<String, ?> oldArgs = ud.getArguments();
      final Map<String, Object> newArgs = new HashMap<>();
      for (final Map.Entry<String, ?> arg : oldArgs.entrySet()) {
        final String name = arg.getKey();
        if ("additionalSources".equals(name) && oldArgs.containsKey("additionalSource"))
          continue;
        if ("additionalSourcesString".equals(name))
          continue;
        newArgs.put(name, arg.getValue());
      }
      return new UninstantiatedDescribable(ud.getSymbol(), ud.getKlass(), newArgs);
    }

  }

  //endregion

}
