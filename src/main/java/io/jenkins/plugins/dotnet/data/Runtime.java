package io.jenkins.plugins.dotnet.data;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.model.DownloadService;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A data file containing the list of valid .NET runtime identifiers. */
@Extension
public final class Runtime extends DownloadService.Downloadable {

  /** Creates a new {@link Runtime} instance. */
  @DataBoundConstructor
  public Runtime() {
  }

  /**
   * Performs auto-completion for a partial runtime identifier.
   *
   * @param text The partial runtime identifier to auto-complete.
   *
   * @return Suitable auto-completion candidates for {@code text}.
   */
  @NonNull
  public AutoCompletionCandidates autoComplete(@CheckForNull String text) {
    final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
    for (final String rid : this.identifiers) {
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
  public FormValidation checkIdentifier(@CheckForNull String text) {
    text = Util.fixEmptyAndTrim(text);
    if (text != null) {
      if (!this.identifiers.contains(text))
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
  @NonNull
  public FormValidation checkIdentifiers(@CheckForNull String text) {
    text = DotNetUtils.normalizeList(text);
    if (text == null)
      return FormValidation.ok();
    final List<FormValidation> result = new ArrayList<>();
    for (final String runtime : text.split(" ")) {
      final FormValidation fv = this.checkIdentifier(runtime);
      if (fv.kind != FormValidation.Kind.OK)
        result.add(fv);
    }
    return FormValidation.aggregate(result);
  }

  //region Internals

  /** The loaded runtime identifiers. */
  private Set<String> identifiers = null;

  /**
   * Gets the (single) instance of {@link Runtime}.
   *
   * @return An instance of {@link Runtime}, loaded with the available .NET runtime identifiers.
   */
  @NonNull
  public static synchronized Runtime getInstance() {
    // JENKINS-62572: would be simpler to pass just the class
    final DownloadService.Downloadable instance = DownloadService.Downloadable.get(Runtime.class.getName());
    if (instance instanceof Runtime)
      return ((Runtime) instance).loadIdentifiers();
    else { // No such downloadable (should be impossible).
      final Runtime empty = new Runtime();
      empty.identifiers = Collections.emptySet();
      return empty;
    }
  }

  /**
   * Loads the runtime identifier data (if not already done).
   *
   * @return This {@link Runtime} instance.
   */
  @NonNull
  private Runtime loadIdentifiers() {
    if (this.identifiers != null)
      return this;
    try {
      final JSONObject json = this.getData();
      if (json != null) {
        this.identifiers = new TreeSet<>();
        for (Object rid : json.getJSONArray("ridCatalog")) {
          if (rid instanceof String)
            this.identifiers.add((String) rid);
        }
      }
    }
    catch (Throwable t) {
      Runtime.LOGGER.log(Level.FINE, Messages.Runtime_LoadFailed(), t);
    }
    finally {
      if (this.identifiers == null)
        this.identifiers = Collections.emptySet();
      if (this.identifiers.isEmpty())
        Runtime.LOGGER.fine(Messages.Runtime_NoData());
    }
    return this;
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Runtime.class.getName());

  //endregion

}
