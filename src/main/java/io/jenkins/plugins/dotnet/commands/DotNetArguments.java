package io.jenkins.plugins.dotnet.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;

import java.io.IOException;

/**
 * Convenience class for handling the adding of command line arguments, including variable expansions and masking of sensitive
 * properties.
 *
 * @deprecated The maintained version lives in the top-level package ({@link io.jenkins.plugins.dotnet.DotNetArguments}).
 */
@Deprecated
@SuppressWarnings("UnusedReturnValue")
public final class DotNetArguments {

  @NonNull
  private final io.jenkins.plugins.dotnet.DotNetArguments arguments;

  /**
   * Creates a new .NET CLI argument processor.
   *
   * @param arguments The "real" .NET argument handler to use.
   */
  DotNetArguments(@NonNull io.jenkins.plugins.dotnet.DotNetArguments arguments) {
    this.arguments = arguments;
  }

  /**
   * Creates a new .NET CLI argument processor.
   *
   * @param run     The execution context.
   * @param cmdLine The underlying argument list builder (expected to be preloaded with the path to the {@code dotnet} executable.
   */
  public DotNetArguments(@NonNull Run<?, ?> run, @NonNull ArgumentListBuilder cmdLine) {
    this.arguments = new io.jenkins.plugins.dotnet.DotNetArguments(run, cmdLine);
  }

  /**
   * Adds a literal string argument.
   *
   * @param value The literal string argument to add.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments add(@NonNull String value) {
    this.arguments.add(value);
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
    this.arguments.add(values);
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
    this.arguments.add(value, mask);
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
    this.arguments.addFlag(flag);
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
    this.arguments.addFlag(flag, set);
    return this;
  }

  /**
   * Adds a flag argument.
   *
   * @param flag The name of the flag (without the {@code -} prefix).
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addFlag(char flag) {
    this.arguments.addFlag(flag);
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
    this.arguments.addFlag(flag, set);
    return this;
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
    this.arguments.addOption(value);
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
    this.arguments.addOption(option, value);
    return this;
  }

  /**
   * Adds an option argument.
   *
   * @param option The name of the option (without the {@code --} prefix).
   * @param value  The option argument to add; it will be converted to its string representation is base 10. If this is
   *               {@code null}, no argument is added.
   *
   * @return This .NET CLI argument processor.
   */
  public DotNetArguments addOption(@NonNull String option, @CheckForNull Integer value) {
    this.arguments.addOption(option, value);
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
    this.arguments.addOption(option, value);
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
    this.arguments.addOption(option, value);
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
    this.arguments.addOptions(values);
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
    this.arguments.addOptions(option, values);
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
    this.arguments.addOptions(option, values);
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
    this.arguments.addOptions(option, values, delimiter);
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
    this.arguments.addPropertyOptions(prefix, propertyString);
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
    this.arguments.addStringCredential(option, value);
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
    return this.arguments.expand(text);
  }

}
