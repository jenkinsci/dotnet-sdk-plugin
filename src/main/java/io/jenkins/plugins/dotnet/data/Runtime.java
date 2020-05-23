package io.jenkins.plugins.dotnet.data;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetUtils;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  @NonNull
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
  @NonNull
  public static FormValidation checkIdentifier(@CheckForNull String text) {
    text = Util.fixEmptyAndTrim(text);
    if (text != null) {
      Runtime.loadIdentifiers();
      if (!Runtime.identifiers.contains(text))
        return FormValidation.error("'%s' is not a valid .NET runtime identifier", text);
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
    // FIXME: This should probably either come from
    // FIXME:   https://github.com/dotnet/runtime/blob/master/src/libraries/pkg/Microsoft.NETCore.Platforms/runtime.json
    // FIXME: or a mirrored version on the Jenkins Update Central.
    try {
      final JSONObject ridCatalog = Data.loadJson(Runtime.class);
      if (ridCatalog != null) {
        @SuppressWarnings("unchecked") final Set<String> rids = ridCatalog.getJSONObject("runtimes").keySet();
        Runtime.identifiers = rids;
      }
    }
    catch (Throwable t) {
      Runtime.LOGGER.log(Level.FINE, "Failed to load RID catalog.", t);
    }
    finally {
      if (Runtime.identifiers == null) {
        Runtime.identifiers = Collections.emptySet();
        Runtime.LOGGER.fine("RID catalog is empty.");
      }
    }
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Runtime.class.getName());

}
