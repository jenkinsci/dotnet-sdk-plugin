package io.jenkins.plugins.dotnet.data;

import hudson.Extension;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.model.DownloadService;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A data file containing the list of valid .NET target framework monikers. */
@Extension
public final class Framework extends DownloadService.Downloadable {

  /** Creates a new {@link Framework} instance. */
  @DataBoundConstructor
  public Framework() {
  }

  /**
   * Performs auto-completion for a partial framework moniker.
   *
   * @param text The partial framework moniker to auto-complete.
   *
   * @return Suitable auto-completion candidates for {@code text}.
   */
  @Nonnull
  public AutoCompletionCandidates autoCompleteMoniker(@CheckForNull String text) {
    final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
    for (final String tfm : this.monikers) {
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
  public FormValidation checkMoniker(@CheckForNull String text) {
    text = Util.fixEmptyAndTrim(text);
    if (text != null) {
      if (!this.monikers.contains(text))
        return FormValidation.error(Messages.Framework_Invalid(text));
    }
    return FormValidation.ok();
  }

  /**
   * Validates a list of framework monikers.
   *
   * @param text The potential framework monikers.
   *
   * @return The validation result.
   */
  @Nonnull
  public FormValidation checkMonikers(@CheckForNull String text) {
    text = DotNetUtils.normalizeList(text);
    if (text == null)
      return FormValidation.ok();
    final List<FormValidation> result = new ArrayList<>();
    for (final String runtime : text.split(" ")) {
      final FormValidation fv = this.checkMoniker(runtime);
      if (fv.kind != FormValidation.Kind.OK)
        result.add(fv);
    }
    return FormValidation.aggregate(result);
  }

  //region Internals

  /** The loaded framework monikers. */
  private Set<String> monikers = null;

  /**
   * Gets the (single) instance of {@link Framework}.
   *
   * @return An instance of {@link Framework}, loaded with the available .NET target framework monikers.
   */
  @Nonnull
  public static synchronized Framework getInstance() {
    // JENKINS-62572: would be simpler to pass just the class
    final DownloadService.Downloadable instance = DownloadService.Downloadable.get(Framework.class.getName());
    if (instance instanceof Framework)
      return ((Framework) instance).loadMonikers();
    else { // No such downloadable (should be impossible).
      final Framework empty = new Framework();
      empty.monikers = Collections.emptySet();
      return empty;
    }
  }

  /**
   * Loads the framework moniker data (if not already done).
   *
   * @return This {@link Framework} instance.
   */
  private Framework loadMonikers() {
    if (this.monikers != null)
      return this;
    try {
      final JSONObject json = this.getData();
      if (json != null) {
        this.monikers = new TreeSet<>();
        for (Object tfm : json.getJSONArray("targetFrameworkMonikers")) {
          if (tfm instanceof String)
            this.monikers.add((String) tfm);
        }
      }
    }
    catch (Throwable t) {
      Framework.LOGGER.log(Level.FINE, Messages.Framework_LoadFailed(), t);
    }
    finally {
      if (this.monikers == null)
        this.monikers = Collections.emptySet();
      if (this.monikers.isEmpty())
        Framework.LOGGER.fine(Messages.Framework_NoData());
    }
    return this;
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Framework.class.getName());

  //endregion

}
