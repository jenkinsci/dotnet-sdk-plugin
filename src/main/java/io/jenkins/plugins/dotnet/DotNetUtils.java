package io.jenkins.plugins.dotnet;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
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
import java.util.Collections;
import java.util.function.Supplier;

/** Utility methods used by the plugin. */
public interface DotNetUtils {

  /** A string variable resolver that does not resolve any variables. */
  VariableResolver<String> RESOLVE_NOTHING = name -> null;

  /**
   * Performs the inverse operation of {@link Util#tokenize(String)}.
   *
   * @param tokens    The tokens to combine.
   * @param delimiter The delimiter to use.
   *
   * @return A single string that tokenizes to the same set of tokens (with empty entries elided), or {@code null} when no tokens
   * were provided.
   */
  @CheckForNull
  static String detokenize(@CheckForNull String[] tokens, char delimiter) {
    if (tokens == null)
      return null;
    final StringBuilder sb = new StringBuilder();
    for (String token : tokens) {
      token = Util.fixEmptyAndTrim(token);
      if (token == null)
        continue;
      if (sb.length() > 0)
        sb.append(delimiter);
      if (token.indexOf(delimiter) >= 0)
        token = Util.singleQuote(token);
      else {
        final String[] subTokens = Util.tokenize(token);
        if (subTokens.length != 1)
          token = Util.singleQuote(token);
      }
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
   * @param context    The context to use to obtain the credentials.
   * @param allowEmpty Indicates whether to include an empty ("no credential selected") option in the listbox.
   *
   * @return A suitably filled listbox model.
   */
  @NonNull
  static ListBoxModel getStringCredentialsList(@CheckForNull Jenkins context, boolean allowEmpty) {
    AbstractIdCredentialsListBoxModel<StandardListBoxModel, StandardCredentials> model = new StandardListBoxModel();
    if (allowEmpty)
      model = model.includeEmptyValue();
    if (context == null || !context.hasPermission(CredentialsProvider.VIEW))
      return model;
    model = model.includeMatchingAs(ACL.SYSTEM, context, StringCredentials.class, Collections.emptyList(), CredentialsMatchers.always());
    return model;
  }

}
