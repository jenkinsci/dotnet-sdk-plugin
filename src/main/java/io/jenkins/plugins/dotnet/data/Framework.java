package io.jenkins.plugins.dotnet.data;

import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utility methods related to framework monikers. */
public abstract class Framework {

  /** The loaded framework monikers. */
  private static Set<String> monikers = null;

  /**
   * Performs auto-completion for a partial framework moniker.
   *
   * @param text The partial framework moniker to auto-complete.
   *
   * @return Suitable auto-completion candidates for {@code text}.
   */
  @Nonnull
  public static AutoCompletionCandidates autoCompleteMoniker(@CheckForNull String text) {
    Framework.loadMonikers();
    final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
    for (final String tfm : Framework.monikers) {
      if (text == null || tfm.startsWith(text))
        candidates.add(tfm);
    }
    return candidates;
  }

  /**
   * Validates a framework moniker.
   *
   * @param text The potential framework moniker.
   *
   * @return The validation result.
   */
  @Nonnull
  public static FormValidation checkMoniker(@CheckForNull String text) {
    text = Util.fixEmptyAndTrim(text);
    if (text != null) {
      Framework.loadMonikers();
      if (!Framework.monikers.contains(text))
        return FormValidation.error(Messages.Framework_Invalid(text));
    }
    return FormValidation.ok();
  }

  /** Loads the framework moniker data (if not already done). */
  private static synchronized void loadMonikers() {
    if (Framework.monikers != null)
      return;
    // FIXME: This should probably come from a file on Jenkins Update Central.
    // FIXME: There seems to be no master JSON file to take the list from, just documentation at
    // FIXME:   https://docs.microsoft.com/en-us/dotnet/standard/frameworks
    // FIXME: which is generated from
    // FIXME:  https://github.com/dotnet/docs/blob/master/docs/standard/frameworks.md
    try {
      final JSONObject json = Data.loadJson(Framework.class);
      if (json != null) {
        final JSONArray array = json.getJSONArray("targetFrameworkMonikers");
        Framework.monikers = new TreeSet<>();
        for (Object tfm : array) {
          if (tfm == null)
            continue;
          if (tfm instanceof String)
            Framework.monikers.add((String) tfm);
        }
      }
    }
    catch (Throwable t) {
      Framework.LOGGER.log(Level.FINE, Messages.Framework_LoadFailed(), t);
    }
    finally {
      if (Framework.monikers == null)
        Framework.monikers = Collections.emptySet();
      if (Framework.monikers.isEmpty())
        Framework.LOGGER.fine(Messages.Framework_NoData());
    }
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Framework.class.getName());

}
