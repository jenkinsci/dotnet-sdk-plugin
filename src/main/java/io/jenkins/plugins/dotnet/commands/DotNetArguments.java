package io.jenkins.plugins.dotnet.commands;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import hudson.util.Secret;
import hudson.util.VariableResolver;
import io.jenkins.plugins.dotnet.DotNetUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Convenience class for handling the adding of command line arguments, including variable expansions and masking of sensitive
 * properties.
 */
@SuppressWarnings("UnusedReturnValue")
public final class DotNetArguments {

  /**
   * Creates a new .NET CLI argument processor.
   *
   * @param run     The execution context.
   * @param cmdLine The underlying argument list builder (expected to be preloaded with the path to the {@code dotnet} executable.
   */
  public DotNetArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder cmdLine) {
    this.run = run;
    if (run instanceof AbstractBuild<?, ?>) {
      final AbstractBuild<?, ?> build = (AbstractBuild<?, ?>) run;
      this.resolver = build.getBuildVariableResolver();
      this.sensitive = build.getSensitiveBuildVariables();
    }
    else { // No variable resolution in code - substitution should be done at the Groovy level
      this.resolver = DotNetUtils.RESOLVE_NOTHING;
      this.sensitive = Collections.emptySet();
    }
    this.cmdLine = cmdLine;
  }

  @NonNull
  private final Run<?, ?> run;

  @NonNull
  private final ArgumentListBuilder cmdLine;

  @NonNull
  private final VariableResolver<String> resolver;

  @NonNull
  private final Set<String> sensitive;

  /**
   * Adds a literal string argument.
   *
   * @param value The literal string argument to add.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments add(@NonNull String value) {
    this.cmdLine.add(value);
    return this;
  }

  /**
   * Adds one or more literal string arguments.
   *
   * @param values The literal string arguments to add.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments add(@NonNull String... values) {
    this.cmdLine.add(values);
    return this;
  }

  /**
   * Adds a literal string argument, potentially masking it.
   *
   * @param value The literal string argument to add.
   * @param mask  Indicates whether the argument should be masked.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments add(@NonNull String value, boolean mask) {
    this.cmdLine.add(value, mask);
    return this;
  }

  /**
   * Adds a flag argument.
   *
   * @param flag The name of the flag (without the {@code --} prefix).
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addFlag(@NonNull String flag) {
    this.cmdLine.add("--" + flag);
    return this;
  }

  /**
   * Optionally adds a flag argument.
   *
   * @param flag The name of the flag (without the {@code --} prefix).
   * @param set  Indicates whether the flag should be added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addFlag(@NonNull String flag, boolean set) {
    return set ? this.addFlag(flag) : this;
  }

  /**
   * Adds a flag argument.
   *
   * @param flag The name of the flag (without the {@code -} prefix).
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addFlag(char flag) {
    this.cmdLine.add("-" + flag);
    return this;
  }

  /**
   * Optionally adds a flag argument.
   *
   * @param flag The name of the flag (without the {@code -} prefix).
   * @param set  Indicates whether the flag should be added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addFlag(char flag, boolean set) {
    return set ? this.addFlag(flag) : this;
  }

  /**
   * Adds an option argument.
   *
   * @param value The option argument to add; it will have variable substitution applied. If it expands to {@code null}, no argument
   *              is added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOption(@CheckForNull String value) {
    value = this.expand(value);
    if (value != null)
      this.cmdLine.add(value);
    return this;
  }

  /**
   * Adds an option argument.
   *
   * @param option The name of the option (without the {@code --} prefix).
   * @param value  The option argument to add; it will be mapped to {@code "true"} or {@code "false"}. If this is {@code null}, no
   *               argument is added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOption(@NonNull String option, @CheckForNull Boolean value) {
    if (value != null)
      this.cmdLine.add("--" + option).add(value ? "true" : "false");
    return this;
  }

  /**
   * Adds an option argument.
   *
   * @param option The name of the option (without the {@code --} prefix).
   * @param value  The option argument to add; it will be converted to its string representation is base 10. If this is {@code
   *               null}, no argument is added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOption(@NonNull String option, @CheckForNull Integer value) {
    if (value != null)
      this.cmdLine.add("--" + option).add(Integer.toString(value, 10));
    return this;
  }

  /**
   * Adds an option argument.
   *
   * @param option The name of the option (without the {@code --} prefix).
   * @param value  The option argument to add; it will have variable substitution applied. If it expands to {@code null}, no
   *               argument is added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOption(@NonNull String option, @CheckForNull String value) {
    value = this.expand(value);
    if (value != null)
      this.cmdLine.add("--" + option).add(value);
    return this;
  }

  /**
   * Adds an option argument.
   *
   * @param option The name of the option (without the {@code -} prefix or the {@code :} suffix).
   * @param value  The option argument to add; it will have variable substitution applied. If it expands to {@code null}, no
   *               argument is added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOption(char option, @CheckForNull String value) {
    value = this.expand(value);
    if (value != null)
      this.cmdLine.add("-" + option + ":" + value);
    return this;
  }

  /**
   * Adds option arguments.
   *
   * @param values A string containing option arguments to add; it will have variable substitution applied and will then be
   *               tokenized to produce the options. Any parts evaluating to {@code null} will not be added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOptions(@CheckForNull String values) {
    values = this.expand(values);
    if (values == null)
      return this;
    for (String value : Util.tokenize(values)) {
      value = Util.fixEmptyAndTrim(value);
      if (value != null)
        this.cmdLine.add(value);
    }
    return this;
  }

  /**
   * Adds option arguments.
   *
   * @param option The name of the option (without the {@code --} prefix).
   * @param values A string containing option arguments to add; it will have variable substitution applied and will then be
   *               tokenized to produce the options. Any parts evaluating to {@code null} will not be added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOptions(@NonNull String option, @CheckForNull String values) {
    values = this.expand(values);
    if (values == null)
      return this;
    for (String value : Util.tokenize(values)) {
      value = Util.fixEmptyAndTrim(value);
      if (value != null)
        this.cmdLine.add("--" + option).add(value);
    }
    return this;
  }

  /**
   * Adds option arguments.
   *
   * @param option The name of the option (without the {@code -} prefix or the {@code :} suffix).
   * @param values A string containing option arguments to add; it will have variable substitution applied and will then be
   *               tokenized to produce the options. Any parts evaluating to {@code null} will not be added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOptions(char option, @CheckForNull String values) {
    values = this.expand(values);
    if (values == null)
      return this;
    for (String value : Util.tokenize(values)) {
      value = Util.fixEmptyAndTrim(value);
      if (value != null)
        this.cmdLine.add("-" + option + ":" + value);
    }
    return this;
  }

  /**
   * Adds option arguments.
   *
   * @param option    The name of the option (without the {@code -} prefix or the {@code :} suffix).
   * @param values    A string containing option arguments to add; it will have variable substitution applied and will then be
   *                  tokenized to produce the options. Any parts evaluating to {@code null} will not be added.
   * @param delimiter The delimiter string to use for tokenization.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOptions(char option, @CheckForNull String values, @NonNull String delimiter) {
    values = this.expand(values);
    if (values == null)
      return this;
    for (String value : Util.tokenize(values, delimiter)) {
      value = Util.fixEmptyAndTrim(value);
      if (value != null)
        this.cmdLine.add("-" + option + ":" + value);
    }
    return this;
  }

  /**
   * Adds option arguments based on a Java-style property string.
   *
   * @param prefix         The prefix to apply to the options.
   * @param propertyString The Java-style property string to process; it will have variable substitution applied.
   *
   * @return This .NET CLI argument processor.
   *
   * @throws IOException When {@code propertyString} could not be loaded as a set of properties.
   */
  public DotNetArguments addPropertyOptions(@NonNull String prefix, @CheckForNull String propertyString) throws IOException {
    this.cmdLine.addKeyValuePairsFromPropertyString(prefix, propertyString, this.resolver, this.sensitive);
    return this;
  }

  /**
   * Adds an option argument containing a string credential.
   *
   * @param option The name of the option (without the {@code --} prefix).
   * @param value  The string credential ID; it will have variable substitution applied. If it expands to {@code null}, no argument
   *               is added.
   *
   * @return This .NET CLI argument processor.
   *
   * @throws AbortException When no matching string credential could be found.
   */
  public DotNetArguments addStringCredential(@NonNull String option, @CheckForNull String value) throws AbortException {
    final Secret secret = this.expandStringCredential(value);
    if (secret != null)
      this.cmdLine.add("--" + option).add(secret, true);
    return this;
  }

  /**
   * Applies variable substitution and whitespace trimming to a string.
   *
   * @param text The text to expand.
   *
   * @return The expanded form of {@code text}.
   */
  public String expand(@CheckForNull String text) {
    if (this.resolver != DotNetUtils.RESOLVE_NOTHING)
      text = Util.replaceMacro(text, this.resolver);
    return Util.fixEmptyAndTrim(text);
  }

  /**
   * Gets the actual secret text associated with a string credential ID.
   *
   * @param value The string credential ID; it will have variable substitution applied.
   *
   * @return The secret text for the given string credential.
   *
   * @throws AbortException When the specified credential could not be found.
   */
  private Secret expandStringCredential(@CheckForNull String value) throws AbortException {
    value = this.expand(value);
    if (value == null)
      return null;
    final StringCredentials credential = CredentialsProvider.findCredentialById(value, StringCredentials.class, this.run);
    if (credential == null)
      throw new AbortException(Messages.DotNetArguments_StringCredentialNotFound(value));
    return credential.getSecret();
  }

}
