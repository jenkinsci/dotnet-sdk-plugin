package io.jenkins.plugins.dotnet.data;

import net.sf.json.JSONObject;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Utility methods for working with metadata (currently text or JSON, stored in resources). */
abstract class Data {

  /**
   * Loads JSON data.
   * <p>
   * This uses {@link #loadText(Class, Charset)} to load the data as UTF-8, then deserializes it.
   *
   * @param c The class to load data for.
   *
   * @return The loaded JSON object, or {@code null} when no data was loaded.
   */
  @CheckForNull
  public static JSONObject loadJson(@Nonnull Class<?> c) {
    final String text = Data.loadText(c, StandardCharsets.UTF_8, ".json");
    if (text == null)
      return null;
    return JSONObject.fromObject(text);
  }

  /**
   * Loads JSON data.
   * <p>
   * This uses {@link #loadText(Class, Charset)} to load the data as UTF-8, then deserializes it.
   *
   * @param c   The class to load data for.
   * @param t   The class to deserialize the data as.
   * @param <T> The type of object to load.
   *
   * @return The loaded JSON object, or {@code null} when no data was loaded.
   */
  @CheckForNull
  public static <T> T loadJson(@Nonnull Class<?> c, @Nonnull Class<T> t) {
    final String text = Data.loadText(c, StandardCharsets.UTF_8, ".json");
    if (text == null)
      return null;
    final JSONObject json = JSONObject.fromObject(text);
    @SuppressWarnings("unchecked") final T object = (T) json.toBean(t);
    return object;
  }

  /**
   * Loads text data.
   *
   * @param c  The class to load data for.
   * @param cs The character set to use.
   *
   * @return The loaded text, or {@code null} when no data was loaded.
   */
  @CheckForNull
  public static String loadText(@Nonnull Class<?> c, @Nonnull Charset cs) {
    return Data.loadText(c, cs, ".txt");
  }

  /**
   * Loads text data.
   *
   * @param c         The class to load data for.
   * @param cs        The character set to use.
   * @param extension The extension to use for the resource file.
   *
   * @return The loaded text, or {@code null} when no data was loaded.
   */
  @CheckForNull
  private static String loadText(@Nonnull Class<?> c, @Nonnull Charset cs, @Nonnull String extension) {
    final String packagePath = c.getPackage().getName().replace('.', '/') + '/';
    final String name = packagePath + c.getSimpleName().toLowerCase() + extension;
    try (final InputStream is = c.getClassLoader().getResourceAsStream(name)) {
      if (is == null)
        return null;
      try (final InputStreamReader isr = new InputStreamReader(is, cs)) {
        try (final BufferedReader reader = new BufferedReader(isr)) {
          return reader.lines().collect(Collectors.joining("\n"));
        }
      }
    }
    catch (Throwable t) {
      Data.LOGGER.log(Level.FINE, String.format("Failed to load resource '%s' as text.", name), t);
      return null;
    }
  }

  /** A logger to use for trace messages. */
  public static final Logger LOGGER = Logger.getLogger(Data.class.getName());

}
