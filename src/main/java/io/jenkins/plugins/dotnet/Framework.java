package io.jenkins.plugins.dotnet;

import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.util.FormValidation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class Framework {

  private static Set<String> monikers = null;

  public static AutoCompletionCandidates autoComplete(String text) {
    Framework.loadIdentifiers();
    final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
    for (String tfm : Framework.monikers) {
      if (text == null || tfm.startsWith(text))
        candidates.add(tfm);
    }
    return candidates;
  }

  public static FormValidation checkMoniker(String framework) {
    framework = Util.fixEmptyAndTrim(framework);
    if (framework != null) {
      Framework.loadIdentifiers();
      if (!Framework.monikers.contains(framework))
        return FormValidation.error("'%s' is not a valid .NET framework moniker", framework);
    }
    return FormValidation.ok();
  }

  private static synchronized void loadIdentifiers() {
    if (Framework.monikers != null)
      return;
    // FIXME: This should probably come from a file on Jenkins Update Central.
    // FIXME: There seems to be no master JSON file to take the list from, just documentation at
    // FIXME:   https://docs.microsoft.com/en-us/dotnet/standard/frameworks
    // FIXME: which is generated from
    // FIXME:  https://github.com/dotnet/docs/blob/master/docs/standard/frameworks.md
    try {
      final JSONObject json = Resource.loadJson(Framework.class, "tfm.json");
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
      LOGGER.log(Level.FINE, "Failed to load .NET framework monikers.", t);
    }
    finally {
      if (monikers == null)
        monikers = Collections.emptySet();
      if (monikers.isEmpty())
        LOGGER.fine("No .NET framework monikers found.");
    }
  }

  private static final Logger LOGGER = Logger.getLogger(Framework.class.getName());

}
