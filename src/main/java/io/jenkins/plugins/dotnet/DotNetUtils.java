package io.jenkins.plugins.dotnet;

import hudson.Util;
import hudson.console.ConsoleNote;
import hudson.util.VariableResolver;
import jenkins.util.JenkinsJVM;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

/** Utility methods used by the plugin. */
public interface DotNetUtils {

  /** A string variable resolver that does not resolve any variables. */
  VariableResolver<String> RESOLVE_NOTHING = name -> null;

  /**
   * Creates an instance of a {@link hudson.console.ConsoleNote} and encodes it as bytes.
   *
   * @param createInstance A means of creating an instance. Typically this will be a constructor reference.
   * @param <T>            The type of console note to encode.
   *
   * @return An instance of a console note, encoded as a byte array.
   */
  @Nonnull
  static <T extends ConsoleNote<?>> byte[] encodeNote(@Nonnull Supplier<T> createInstance) {
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
   * Normalizes a list using whitespace, commas and/or semicolons to use a single space.
   *
   * @param list The list to normalize.
   *
   * @return The normalized list, or {@code null} when {@code list} was {@code null}.
   */
  @CheckForNull
  static String normalizeList(@CheckForNull String list) {
    if (list == null)
      return null;
    list = list.replaceAll("(?:\\s|[,;])+", " ");
    return Util.fixEmptyAndTrim(list);
  }

}
