package io.jenkins.plugins.dotnet.commands.nuget;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.AbortException;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;
import io.jenkins.plugins.dotnet.commands.Command;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/** A build step executing a subcommand of {@code dotnet nuget}. */
public abstract class NuGetCommand extends Command {

  protected static void addApiKeyOption(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull String option, @Nullable String value) throws AbortException {
    if (value == null)
      return;
    final StringCredentials credential = CredentialsProvider.findCredentialById(value, StringCredentials.class, run);
    if (credential == null)
      throw new AbortException(Messages.NuGet_Command_NoCredentialsFound(value));
    args.add(option).add(credential.getSecret(), true);
  }

  /**
   * Adds command line arguments for this .NET NuGet command invocation.
   * <p>
   * This adds:
   * <ol>
   *   <li>{@code --force-english-output}, if requested via {@link #setForceEnglishOutput(boolean)}.</li>
   * </ol>
   */
  @Override
  protected void addCommandLineArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) throws AbortException {
    if (this.forceEnglishOutput)
      args.add("--force-english-output");
  }

  //region Properties

  protected boolean forceEnglishOutput;

  public boolean isForceEnglishOutput() {
    return this.forceEnglishOutput;
  }

  @DataBoundSetter
  public void setForceEnglishOutput(boolean forceEnglishOutput) {
    this.forceEnglishOutput = forceEnglishOutput;
  }

  //endregion

}
