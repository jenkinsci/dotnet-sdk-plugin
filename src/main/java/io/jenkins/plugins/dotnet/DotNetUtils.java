package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.Util;

/** Utility methods used by the plugin. */
public interface DotNetUtils {

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
