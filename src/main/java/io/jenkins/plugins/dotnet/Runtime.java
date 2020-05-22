package io.jenkins.plugins.dotnet;

import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class Runtime {

  private static Set<String> identifiers = null;

  public static AutoCompletionCandidates autoComplete(String text) {
    Runtime.loadIdentifiers();
    final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
    for (String rid : Runtime.identifiers) {
      if (text == null || rid.startsWith(text))
        candidates.add(rid);
    }
    return candidates;
  }

  public static FormValidation checkIdentifier(String runtime) {
    runtime = Util.fixEmptyAndTrim(runtime);
    if (runtime != null) {
      Runtime.loadIdentifiers();
      if (!Runtime.identifiers.contains(runtime))
        return FormValidation.error("'%s' is not a valid runtime identifier", runtime);
    }
    return FormValidation.ok();
  }

  public static FormValidation checkIdentifiers(@CheckForNull String runtimes) {
    runtimes = DotNet.normalizeList(runtimes);
    if (runtimes == null)
      return FormValidation.ok();
    final List<FormValidation> result = new ArrayList<>();
    for (final String runtime : runtimes.split(" ")) {
      final FormValidation fv = Runtime.checkIdentifier(runtime);
      if (fv.kind != FormValidation.Kind.OK)
        result.add(fv);
    }
    return FormValidation.aggregate(result);
  }

  private static synchronized void loadIdentifiers() {
    if (Runtime.identifiers != null)
      return;
    // FIXME: This should probably either come from
    // FIXME:   https://github.com/dotnet/runtime/blob/master/src/libraries/pkg/Microsoft.NETCore.Platforms/runtime.json
    // FIXME: or a mirrored version on the Jenkins Update Central.
    try {
      final JSONObject ridCatalog = Resource.loadJson(Runtime.class, "runtime.json");
      if (ridCatalog != null) {
        @SuppressWarnings("unchecked")
        final Set<String> rids = ridCatalog.getJSONObject("runtimes").keySet();
        Runtime.identifiers = rids;
      }
    }
    catch (Throwable t) {
      LOGGER.log(Level.FINE, "Failed to load RID catalog.", t);
    }
    finally {
      if (identifiers == null) {
        identifiers = Collections.emptySet();
        LOGGER.fine("RID catalog is empty.");
      }
    }
  }

  private static final Logger LOGGER = Logger.getLogger(Runtime.class.getName());

}
