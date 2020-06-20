package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Functions;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.AutoCompletionCandidates;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.VariableResolver;
import io.jenkins.plugins.dotnet.console.DiagnosticScanner;
import io.jenkins.plugins.dotnet.data.Framework;
import io.jenkins.plugins.dotnet.data.Runtime;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** A builder executing a .NET CLI command. */
public abstract class DotNet extends Builder {

  /** {@inheritDoc} */
  @Override
  public CommandDescriptor getDescriptor() {
    return (CommandDescriptor) super.getDescriptor();
  }

  /**
   * Adds command line arguments for this invocation of the {@code dotnet} CLI.
   *
   * @param args      The current set of arguments.
   * @param resolver  The variable resolved to use.
   * @param sensitive The list of variable names whose content is to be considered sensitive.
   */
  protected abstract void addCommandLineArguments(@NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive);

  /** Gets the descriptor for a .NET SDK. */
  @NonNull
  public static DotNetSDK.DescriptorImpl getSdkDescriptor() throws AbortException {
    // spotbugs does not like explicit throws of NullPointerException (see https://github.com/spotbugs/spotbugs/issues/1175), so we
    // need to do the test explicitly instead of using Objects.requireNonNull.
    final DotNetSDK.DescriptorImpl sdkDescriptor = ToolInstallation.all().get(DotNetSDK.DescriptorImpl.class);
    if (sdkDescriptor == null)
      throw new AbortException(".NET SDK descriptor not found.");
    return sdkDescriptor;
  }

  /** {@inheritDoc} */
  @Override
  public boolean perform(@NonNull AbstractBuild<?, ?> build, @NonNull Launcher launcher, @NonNull BuildListener listener) throws InterruptedException, IOException {
    final FilePath workspace = build.getWorkspace();
    if (workspace == null)
      throw new AbortException(Messages.DotNet_NoWorkspace());
    final EnvVars env = build.getEnvironment(listener);
    for (Map.Entry<String, String> e : build.getBuildVariables().entrySet())
      env.put(e.getKey(), e.getValue());
    final Result r = this.run(workspace, env, launcher, listener, build.getCharset());
    if (r != Result.SUCCESS)
      build.setResult(r);
    return true;
  }

  /**
   * Runs this .NET command.
   *
   * @param wd       The base working directory for the command.
   * @param env      The environment variables that apply for the command.
   * @param launcher The launcher to use to execute the command.
   * @param listener The listener to report command output to.
   * @param cs       The character set to use for command output.
   *
   * @return The command's result.
   * @throws InterruptedException When execution is interrupted.
   * @throws IOException          When an I/O error occurs.
   */
  @NonNull
  public Result run(@NonNull FilePath wd, @NonNull EnvVars env, @NonNull Launcher launcher, @NonNull TaskListener listener, @NonNull Charset cs) throws InterruptedException, IOException {
    final DotNetSDK sdkInstance;
    if (this.sdk == null)
      sdkInstance = null;
    else
      sdkInstance = DotNet.getSdkDescriptor().prepareAndValidateInstance(this.sdk, wd, env, listener);
    final String executable;
    if (sdkInstance != null) {
      executable = sdkInstance.ensureExecutableExists(launcher);
      sdkInstance.buildEnvVars(env);
    }
    else {
      final String basename = DotNetSDK.getExecutableFileName(launcher);
      {
        final String home = Util.fixEmptyAndTrim(env.get(DotNetSDK.HOME_ENVIRONMENT_VARIABLE, ""));
        if (home != null) // construct the full remote path
          executable = new FilePath(launcher.getChannel(), home).child(basename).absolutize().getRemote();
        else // will have to rely on the system's PATH
          executable = basename;
      }
    }
    if (this.workDirectory != null)
      wd = wd.child(this.workDirectory);
    try {
      if (sdkInstance != null && this.specificSdkVersion)
        sdkInstance.createGlobalJson(wd, listener);
      // Note: this MUST NOT BE CLOSED, because that also closes the build listener, causing all further output to go bye-bye
      final DiagnosticScanner scanner = new DiagnosticScanner(listener.getLogger(), cs);
      if (this.showSdkInfo) {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable, "--info");
        launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
      }
      int rc = -1;
      {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable);
        { // Add the rest of the command line. Will eventually support variable expansion.
          final VariableResolver<String> vr = DotNetUtils.RESOLVE_NOTHING;
          final Set<String> sensitive = Collections.emptySet();
          this.addCommandLineArguments(cmdLine, vr, sensitive);
        }
        try {
          rc = launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
        }
        finally {
          scanner.writeCompletionMessage(rc);
        }
      }
      if (this.shutDownBuildServers) {
        final ArgumentListBuilder cmdLine = new ArgumentListBuilder(executable, "build-server", "shutdown");
        launcher.launch().cmds(cmdLine).envs(env).stdout(scanner).pwd(wd).join();
      }
      // TODO: Maybe also add configuration to set the build as either failed or unstable based on return code
      if (rc != 0)
        return Result.FAILURE;
      if (scanner.getErrors() > 0)
        return Result.FAILURE;
      if (this.unstableIfWarnings && scanner.getWarnings() > 0)
        return Result.UNSTABLE;
      return Result.SUCCESS;
    }
    catch (Throwable t) {
      Functions.printStackTrace(t, listener.fatalError(Messages.DotNet_ExecutionFailed()));
      throw new AbortException(Messages.DotNet_ExecutionFailed());
    }
    finally {
      if (sdkInstance != null && this.specificSdkVersion)
        DotNetSDK.removeGlobalJson(wd, listener);
    }
  }

  //region Properties

  /** The name of the SDK to use. */
  @CheckForNull
  protected String sdk;

  /**
   * Gets the name of the SDK to use.
   *
   * @return The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @CheckForNull
  public String getSdk() {
    return this.sdk;
  }

  /**
   * Sets the name of the SDK to use.
   *
   * @param sdk The name of the SDK to use, or {@code null} to use the SDK made available by the parent context (or the system).
   */
  @DataBoundSetter
  public void setSdk(@CheckForNull String sdk) {
    this.sdk = Util.fixEmpty(sdk);
  }

  /** Flag indicating whether or not SDK information should be shown. */
  protected boolean showSdkInfo = false;

  /**
   * Determines whether or not SDK information should be shown.
   *
   * @return {@code true} if "{@code dotnet --info}" should be run before the main command; {@code false} otherwise.
   */
  public boolean isShowSdkInfo() {
    return this.showSdkInfo;
  }

  /**
   * Determines whether or not SDK information should be shown.
   *
   * @param showSdkInfo {@code true} if "{@code dotnet --info}" should be run before the main command; {@code false} otherwise.
   */
  @DataBoundSetter
  public void setShowSdkInfo(boolean showSdkInfo) {
    this.showSdkInfo = showSdkInfo;
  }

  /** Flag indicating whether or not any build servers started by the main command should be shut down. */
  protected boolean shutDownBuildServers = false;

  /** Flag indicating whether or not a specific SDK version should be used. */
  private boolean specificSdkVersion = false;

  /**
   * Determines whether or not a specific SDK version should be used.
   *
   * @return {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK (as opposed to
   * a more recent one that happens to be installed on the build agent); {@code false} otherwise.
   */
  public boolean isSpecificSdkVersion() {
    return this.specificSdkVersion;
  }

  /**
   * Determines whether or not a specific SDK version should be used.
   *
   * @param specificSdkVersion {@code true} if a {@code global.json} should be created to force the use of the configured .NET SDK
   *                           (as opposed to a more recent one that happens to be installed on the build agent); {@code false}
   *                           otherwise.
   */
  @DataBoundSetter
  public void setSpecificSdkVersion(boolean specificSdkVersion) {
    this.specificSdkVersion = specificSdkVersion;
  }

  /** Flag indicating whether or not the presence of warnings makes the build unstable. */
  protected boolean unstableIfWarnings = false;

  /** The working directory to use for the command. This directory is <em>not</em> created by the command execution. */
  @CheckForNull
  protected String workDirectory = null;

  /**
   * Gets the working directory to use for the command.
   *
   * @return The working directory to use for the command.
   */
  @CheckForNull
  public String getWorkDirectory() {
    return this.workDirectory;
  }

  /**
   * Sets the working directory to use for the command.
   *
   * @param workDirectory The working directory to use for the command.
   */
  @DataBoundSetter
  public void setWorkDirectory(@CheckForNull String workDirectory) {
    this.workDirectory = Util.fixEmpty(workDirectory);
  }

  //endregion

  //region Command Descriptor

  /** A descriptor for a .NET command. */
  public static abstract class CommandDescriptor extends BuildStepDescriptor<Builder> {

    /**
     * Creates a new .NET command descriptor instance.
     * <p>
     * This version works when you follow the common convention, where a descriptor is written as the static nested class of the
     * describable class.
     */
    protected CommandDescriptor() {
    }

    /**
     * Creates a new .NET command descriptor instance for a specific class.
     *
     * @param clazz The class implementing the command described by this descriptor instance.
     */
    protected CommandDescriptor(@NonNull Class<? extends DotNet> clazz) {
      super(clazz);
    }

    /**
     * Performs auto-completion for a .NET target framework moniker.
     *
     * @param value The (partial) value to perform auto-completion for.
     *
     * @return The computed auto-completion candidates.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final AutoCompletionCandidates doAutoCompleteFramework(@CheckForNull @QueryParameter String value) {
      return Framework.getInstance().autoCompleteMoniker(value);
    }

    /**
     * Performs auto-completion for a list of .NET target framework monikers.
     *
     * @param value The (partial) value to perform auto-completion for.
     *
     * @return The computed auto-completion candidates.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final AutoCompletionCandidates doAutoCompleteFrameworks(@CheckForNull @QueryParameter String value) {
      return Framework.getInstance().autoCompleteMoniker(value);
    }

    /**
     * Performs auto-completion for a .NET target runtime identifier.
     *
     * @param value The (partial) value to perform auto-completion for.
     *
     * @return The computed auto-completion candidates.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final AutoCompletionCandidates doAutoCompleteRuntime(@CheckForNull @QueryParameter String value) {
      return Runtime.getInstance().autoCompleteIdentifier(value);
    }

    /**
     * Performs auto-completion for a list of .NET runtime identifiers.
     *
     * @param value The (partial) value to perform auto-completion for.
     *
     * @return The computed auto-completion candidates.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final AutoCompletionCandidates doAutoCompleteRuntimes(@CheckForNull @QueryParameter String value) {
      return Runtime.getInstance().autoCompleteIdentifier(value);
    }

    /**
     * Performs validation on a .NET target framework moniker.
     *
     * @param value The value to validate.
     *
     * @return The result of the validation.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckFramework(@CheckForNull @QueryParameter String value) {
      return Framework.getInstance().checkMoniker(value);
    }

    /**
     * Performs validation on a list of .NET target framework monikers.
     *
     * @param value The value to validate.
     *
     * @return The result of the validation.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckFrameworks(@CheckForNull @QueryParameter String value) {
      return Framework.getInstance().checkMonikers(value);
    }

    /**
     * Performs validation on a .NET runtime identifier.
     *
     * @param value The value to validate.
     *
     * @return The result of the validation.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckRuntime(@CheckForNull @QueryParameter String value) {
      return Runtime.getInstance().checkIdentifier(value);
    }

    /**
     * Performs validation on a list of .NET runtime identifiers.
     *
     * @param value The values to validate.
     *
     * @return The result of the validation.
     */
    @SuppressWarnings("unused")
    @NonNull
    public FormValidation doCheckRuntimes(@CheckForNull @QueryParameter String value) {
      return Runtime.getInstance().checkIdentifiers(value);
    }

    /**
     * Fills a listbox with the names of .NET SDKs that have been defined as global tools.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final ListBoxModel doFillSdkItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_DefaultSDK(), "");
      DotNetSDK.addSdks(model);
      return model;
    }

    /**
     * Fills a listbox with the possible values for the .NET CLI "verbosity" option.
     *
     * @return A suitably filled listbox model.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final ListBoxModel doFillVerbosityItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNet_Verbosity_Default(), "");
      model.add(Messages.DotNet_Verbosity_Quiet(), "q");
      model.add(Messages.DotNet_Verbosity_Minimal(), "m");
      model.add(Messages.DotNet_Verbosity_Normal(), "n");
      model.add(Messages.DotNet_Verbosity_Detailed(), "d");
      model.add(Messages.DotNet_Verbosity_Diagnostic(), "diag");
      return model;
    }

    /**
     * Gets the button text to use for the "Advanced" button.
     *
     * @return "More Options", or the localized equivalent.
     */
    @SuppressWarnings("unused")
    @NonNull
    public final String getMoreOptions() {
      return Messages.DotNet_MoreOptions();
    }

    /**
     * Determines whether or not this descriptor is applicable for the specified job type.
     *
     * @param jobType The job type.
     *
     * @return {@code true}.
     */
    public final boolean isApplicable(@CheckForNull Class<? extends AbstractProject> jobType) {
      return true;
    }

  }

  //endregion

}
