package io.jenkins.plugins.dotnet.data;

import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Utility methods related to runtime identifiers. */
public abstract class Runtime {

  /** The loaded runtime identifiers. */
  private static Set<String> identifiers = null;

  /**
   * Performs auto-completion for a partial runtime identifier.
   *
   * @param text The partial runtime identifier to auto-complete.
   *
   * @return Suitable auto-completion candidates for {@code text}.
   */
  @Nonnull
  public static AutoCompletionCandidates autoComplete(@CheckForNull String text) {
    Runtime.loadIdentifiers();
    final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
    for (final String rid : Runtime.identifiers) {
      if (text == null || rid.startsWith(text))
        candidates.add(rid);
    }
    return candidates;
  }

  /**
   * Validates a runtime identifier.
   *
   * @param text The potential runtime identifier.
   *
   * @return The validation result.
   */
  @Nonnull
  public static FormValidation checkIdentifier(@CheckForNull String text) {
    text = Util.fixEmptyAndTrim(text);
    if (text != null) {
      Runtime.loadIdentifiers();
      if (!Runtime.identifiers.contains(text))
        return FormValidation.error(Messages.Runtime_Invalid(text));
    }
    return FormValidation.ok();
  }

  /**
   * Validates a list of runtime identifiers.
   *
   * @param text The potential runtime identifiers.
   *
   * @return The validation result.
   */
  public static FormValidation checkIdentifiers(@CheckForNull String text) {
    text = DotNetUtils.normalizeList(text);
    if (text == null)
      return FormValidation.ok();
    final List<FormValidation> result = new ArrayList<>();
    for (final String runtime : text.split(" ")) {
      final FormValidation fv = Runtime.checkIdentifier(runtime);
      if (fv.kind != FormValidation.Kind.OK)
        result.add(fv);
    }
    return FormValidation.aggregate(result);
  }

  /** Loads the runtime identifier data (if not already done). */
  private static synchronized void loadIdentifiers() {
    if (Runtime.identifiers != null)
      return;
    try {
      // TODO: Switch this to using a Downloadable once the crawler PR is submitted and approved.
      final JSONObject json = Data.loadJson(Runtime.class);
      if (json != null) {
        Runtime.identifiers = new TreeSet<>();
        for (Object rid : json.getJSONArray("ridCatalog")) {
          if (rid instanceof String)
            Runtime.identifiers.add((String) rid);
        }
      }
    }
    catch (Throwable t) {
      Runtime.LOGGER.log(Level.FINE, Messages.Runtime_LoadFailed(), t);
    }
    finally {
      if (Runtime.identifiers == null) {
        Runtime.identifiers = Collections.emptySet();
        Runtime.LOGGER.fine(Messages.Runtime_NoData());
      }
    }
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Runtime.class.getName());

}
