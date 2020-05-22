package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import net.sf.json.JSONObject;

import javax.annotation.CheckForNull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

interface Resource {

  @CheckForNull
  static JSONObject loadJson(@NonNull Class<?> c, @NonNull String name) {
    final String text = loadString(c, name);
    if (text == null)
      return null;
    return JSONObject.fromObject(text);
  }

  static String loadString(@NonNull Class<?> c, @NonNull String name) {
    name = c.getPackage().getName().replace('.', '/') + '/' + name;
    try (final InputStream is = c.getClassLoader().getResourceAsStream(name)) {
      if (is == null)
        return null;
      try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
           BufferedReader reader = new BufferedReader(isr)) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
    catch (Throwable t) {
      LOGGER.log(Level.FINE, String.format("Failed to load resource '%s' as string.", name), t);
      return null;
    }
  }

  Logger LOGGER = Logger.getLogger(Resource.class.getName());

}
