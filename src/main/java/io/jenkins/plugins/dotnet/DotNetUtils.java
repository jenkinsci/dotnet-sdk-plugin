package io.jenkins.plugins.dotnet;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.console.ConsoleNote;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.VariableResolver;
import jenkins.model.Jenkins;
import jenkins.util.JenkinsJVM;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** Utility methods used by the plugin. */
public interface DotNetUtils {

  /** A string variable resolver that does not resolve any variables. */
  VariableResolver<String> RESOLVE_NOTHING = name -> null;

  @CheckForNull
  static Map<String, String> createPropertyMap(@CheckForNull String propertyString) throws IOException {
    if (Util.fixEmpty(propertyString) == null)
      return null;
    final Properties properties = Util.loadProperties(propertyString);
    return properties.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
  }

  @CheckForNull
  static String createPropertyString(@CheckForNull Map<String, String> propertyMap) throws IOException {
    if (propertyMap == null || propertyMap.isEmpty())
      return null;
    final Properties properties = new Properties();
    properties.putAll(propertyMap);
    try (final StringWriter sw = new StringWriter()) {
      properties.store(sw, null);
      return sw.toString().replaceAll("^#.*?\r?\n", "");
    }
  }

  /**
   * Performs the inverse operation of {@link DotNetUtils#tokenize(String)} for a single token.
   *
   * @param delimiter The delimiter to use.
   * @param token     The token to process.
   *
   * @return A single string that tokenizes to the same single token, or {@code null} when no token was provided were provided.
   */
  @CheckForNull
  static String detokenize(char delimiter, @CheckForNull String token) {
    token = Util.fixEmptyAndTrim(token);
    if (token == null)
      return null;
    final boolean needQuoting;
    if (token.indexOf(delimiter) >= 0)
      needQuoting = true;
    else {
      final String[] subTokens = Util.tokenize(token);
      needQuoting = subTokens.length != 1;
    }
    if (needQuoting) // FIXME: This is not ideal. A smarter method that uses either ' or " based on nicest result would be useful.
      token = Util.singleQuote(token.replace("'", "\\'"));
    return token;
  }

  /**
   * Performs the inverse operation of {@link DotNetUtils#tokenize(String)}.
   *
   * @param delimiter The delimiter to use.
   * @param tokens    The tokens to combine.
   *
   * @return A single string that tokenizes to the same set of tokens (with empty entries elided), or {@code null} when no tokens
   * were provided.
   */
  @CheckForNull
  static String detokenize(char delimiter, @CheckForNull String... tokens) {
    if (tokens == null || tokens.length == 0)
      return null;
    if (tokens.length == 1)
      return DotNetUtils.detokenize(delimiter, tokens[0]);
    final StringBuilder sb = new StringBuilder();
    for (String token : tokens) {
      token = DotNetUtils.detokenize(delimiter, token);
      if (token == null)
        continue;
      if (sb.length() > 0)
        sb.append(delimiter);
      sb.append(token);
    }
    if (sb.length() == 0)
      return null;
    return sb.toString();
  }

  /**
   * Creates an instance of a {@link hudson.console.ConsoleNote} and encodes it as bytes.
   *
   * @param createInstance A means of creating an instance. Typically this will be a constructor reference.
   * @param <T>            The type of console note to encode.
   *
   * @return An instance of a console note, encoded as a byte array.
   */
  @NonNull
  static <T extends ConsoleNote<?>> byte[] encodeNote(@NonNull Supplier<T> createInstance) {
    JenkinsJVM.checkJenkinsJVM();
    try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
      createInstance.get().encodeTo(bytes);
      return bytes.toByteArray();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Fills a listbox model with all available string credentials.
   *
   * @param allowEmpty Indicates whether to include an empty ("no credential selected") option in the listbox.
   *
   * @return A suitably filled listbox model.
   */
  @NonNull
  @SuppressWarnings("deprecation")
  static ListBoxModel getStringCredentialsList(boolean allowEmpty) {
    AbstractIdCredentialsListBoxModel<StandardListBoxModel, StandardCredentials> model = new StandardListBoxModel();
    if (allowEmpty) {
      model = model.includeEmptyValue();
    }
    final Jenkins context = Jenkins.getInstanceOrNull();
    if (context == null || !context.hasPermission(CredentialsProvider.VIEW)) {
      return model;
    }
    // This should be ACL.SYSTEM2, but the Credential API has not yet been updated to use Spring Security.
    model = model.includeAs(ACL.SYSTEM, context, StringCredentials.class);
    return model;
  }

  /**
   * Tokenizes text separated by (default) delimiters and returns the sole resulting token.
   *
   * @param s A string containing tokens.
   *
   * @return The sole token contained in {@code s}, or {@code null} when it did not contain exactly one token.
   *
   * @see Util#tokenize(String)
   */
  @CheckForNull
  static String singleToken(@CheckForNull String s) {
    final String[] tokens = DotNetUtils.tokenize(s);
    if (tokens == null || tokens.length != 1)
      return null;
    return tokens[0];
  }

  /**
   * Tokenizes text separated by delimiters and returns the sole resulting token.
   *
   * @param s          A string containing tokens.
   * @param delimiters A string containing the delimiters to use.
   *
   * @return The sole token contained in {@code s}, or {@code null} when it did not contain exactly one token.
   *
   * @see Util#tokenize(String, String)
   */
  @CheckForNull
  static String singleToken(@CheckForNull String s, @CheckForNull String delimiters) {
    final String[] tokens = DotNetUtils.tokenize(s, delimiters);
    if (tokens == null || tokens.length != 1)
      return null;
    return tokens[0];
  }

  /**
   * Tokenizes text separated by (default) delimiters.
   *
   * @param s A string containing tokens.
   *
   * @return The tokens contained in {@code s}, or {@code null} when it did not contain any.
   *
   * @see Util#tokenize(String)
   */
  @CheckForNull
  static String[] tokenize(@CheckForNull String s) {
    if (s == null)
      return null;
    final String[] tokens = Util.tokenize(s);
    if (tokens.length == 0)
      return null;
    return tokens;
  }

  /**
   * Tokenizes text separated by delimiters.
   *
   * @param s          A string containing tokens.
   * @param delimiters A string containing the delimiters to use.
   *
   * @return The tokens contained in {@code s}, or {@code null} when it did not contain any.
   *
   * @see Util#tokenize(String, String)
   */
  @CheckForNull
  static String[] tokenize(@CheckForNull String s, @CheckForNull String delimiters) {
    if (s == null)
      return null;
    final String[] tokens = Util.tokenize(s, delimiters);
    if (tokens.length == 0)
      return null;
    return tokens;
  }

}
