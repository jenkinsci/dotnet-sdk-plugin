package io.jenkins.plugins.dotnet.data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.DownloadService;
import hudson.model.ModelObject;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.ezmorph.Morpher;
import net.sf.json.JSONObject;
import net.sf.json.util.EnumMorpher;
import net.sf.json.util.JSONUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A data file containing the available .NET SDK installer packages. */
@Extension
public final class Downloads extends DownloadService.Downloadable {

  /** Creates a new {@link Downloads} downloadable. */
  @DataBoundConstructor
  public Downloads() {
  }

  //region Lookup Methods

  /**
   * Gets a package for a particular SDK, via its download link.
   *
   * @param sdk The SDK to which the package belongs.
   * @param url The download link for the package.
   *
   * @return The requested package, or {@code null} if it was not found.
   */
  @CheckForNull
  private static Package getPackage(@CheckForNull Sdk sdk, @CheckForNull String url) {
    if (sdk == null)
      return null;
    return sdk.getPackage(url);
  }

  /**
   * Gets a package for a particular SDK, via its download link.
   *
   * @param sdk The name of the SDK to which the package belongs.
   * @param url The download link for the package.
   *
   * @return The requested package, or {@code null} if it was not found.
   */
  @CheckForNull
  public Package getPackage(@CheckForNull String sdk, @CheckForNull String url) {
    return Downloads.getPackage(this.getSdk(sdk), url);
  }

  /**
   * Gets a package for a particular SDK, via its download link.
   *
   * @param version The name of the version containing the release to which the package belongs.
   * @param release The name of the release containing the SDK to which the package belongs.
   * @param sdk     The name of the SDK to which the package belongs.
   * @param url     The download link for the package.
   *
   * @return The requested package, or {@code null} if it was not found.
   */
  @CheckForNull
  public Package getPackage(@CheckForNull String version, @CheckForNull String release, @CheckForNull String sdk, @CheckForNull String url) {
    return Downloads.getPackage(this.getSdk(version, release, sdk), url);
  }

  /**
   * Gets a release, via its name.
   *
   * @param version The name of the version containing the release.
   * @param name    The name of the release.
   *
   * @return The requested release, or {@code null} if it was not found.
   */
  @CheckForNull
  public Release getRelease(@CheckForNull String version, @CheckForNull String name) {
    final Version v = this.getVersion(version);
    if (v == null)
      return null;
    return v.getRelease(name);
  }

  /**
   * Gets an SDK, via its name.
   *
   * @param name The name of the SDK.
   *
   * @return The requested SDK, or {@code null} if it was not found.
   */
  @CheckForNull
  public Sdk getSdk(@CheckForNull String name) {
    return this.sdks.get(name);
  }

  /**
   * Gets a release-specific SDK, via its name.
   *
   * @param version The name of the version containing the release to which the SDK belongs.
   * @param release The name of the release containing the SDK.
   * @param name    The name of the SDK.
   *
   * @return The requested SDK, or {@code null} if it was not found.
   */
  @CheckForNull
  public Sdk getSdk(@CheckForNull String version, @CheckForNull String release, @CheckForNull String name) {
    final Release r = this.getRelease(version, release);
    if (r == null || r.sdks == null || !r.sdks.contains(name))
      return null;
    return this.getSdk(name);
  }

  /**
   * Gets a version, via its name.
   *
   * @param name The name of the version.
   *
   * @return The requested version, or {@code null} if it was not found.
   */
  @CheckForNull
  public Version getVersion(@CheckForNull String name) {
    return this.versions.get(name);
  }

  //endregion

  //region List Box Methods

  /**
   * Adds the available packages for a specific SDK to a list box.
   *
   * @param model The list box to add the packages to.
   * @param sdk   The name of the SDK containing the packages to add.
   *
   * @return The updated list box.
   */
  @Nonnull
  public ListBoxModel addPackages(@Nonnull ListBoxModel model, @CheckForNull String sdk) {
    final Sdk s = this.getSdk(sdk);
    if (s != null && s.packages != null) {
      for (Package p : s.packages)
        model.add(p, p.url);
    }
    return model;
  }

  /**
   * Adds the available releases for a specific version to a list box.
   *
   * @param model          The list box to add the releases to.
   * @param version        The name of the version containing the releases to add.
   * @param includePreview Indicates whether or not preview releases should be included.
   *
   * @return The updated list box.
   */
  @Nonnull
  public ListBoxModel addReleases(@Nonnull ListBoxModel model, @CheckForNull String version, boolean includePreview) {
    final Version v = this.getVersion(version);
    if (v != null && v.releases != null) {
      for (Release r : v.releases) {
        if (r.preview && !includePreview)
          continue;
        model.add(r, r.name);
      }
    }
    return model;
  }

  /**
   * Adds the available SDKs for a specific release to a list box.
   *
   * @param model   The list box to add the SDKs to.
   * @param version The name of the version containing the release.
   * @param release The name of the release containing the SDKs to add.
   *
   * @return The updated list box.
   */
  @Nonnull
  public ListBoxModel addSdks(@Nonnull ListBoxModel model, @CheckForNull String version, @CheckForNull String release) {
    final Release r = this.getRelease(version, release);
    if (r != null && r.sdks != null) {
      for (String sdk : r.sdks) {
        final Sdk s = this.getSdk(sdk);
        if (s != null)
          model.add(s, s.name);
      }
    }
    return model;
  }

  /**
   * Adds the available .NET versions to a list box.
   *
   * @param model The list box to add the versions to.
   *
   * @return The updated list box.
   */
  @Nonnull
  public ListBoxModel addVersions(@Nonnull ListBoxModel model) {
    for (Version v : this.versions.values())
      model.add(v, v.name);
    return model;
  }

  //endregion

  //region Nested Types

  //region Version

  /** A .NET version. */
  public static final class Version implements ModelObject {

    //region Status Enum

    /** The support status of a .NET version. */
    public enum Status {

      /** This version is current and supported. */
      CURRENT,

      /** This version has reached end-of-life. */
      EOL,

      /** This is a version with long-term support. */
      LTS,

      /** The version is no longer current, and will only get security fixes before reaching end-of-life. */
      MAINTENANCE,

      /** This is a preview of the next version of .NET. */
      PREVIEW,

      /** The status of the version is unknown. */
      UNKNOWN,

      ;

      /**
       * Maps this status to a descriptive string.
       *
       * @return A string describing this status.
       */
      @Nonnull
      public String getDisplayName() {
        switch (this) {
          case CURRENT:
            return Messages.Downloads_Version_Status_Current();
          case EOL:
            return Messages.Downloads_Version_Status_EOL();
          case LTS:
            return Messages.Downloads_Version_Status_LTS();
          case MAINTENANCE:
            return Messages.Downloads_Version_Status_Maintenance();
          case PREVIEW:
            return Messages.Downloads_Version_Status_Preview();
          case UNKNOWN:
            return Messages.Downloads_Version_Status_Unknown();
        }
        return null;
      }

    }

    //endregion

    /** The name of the version. */
    public String name;

    /** The status of the version. */
    public Status status;

    /** The date on which support for this version ends (or ended), if known. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String endOfSupport;

    /** The releases for this version. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public Release[] releases;

    private final Map<String, Release> releaseMap = new HashMap<>();

    private void finish() {
      if (this.name == null)
        this.name = Messages.Downloads_Unknown();
      if (this.status == null)
        this.status = Status.UNKNOWN;
      if (!this.releaseMap.isEmpty())
        this.releaseMap.clear();
      if (this.releases == null)
        return;
      for (final Release r : this.releases) {
        r.finish();
        this.releaseMap.put(r.name, r);
      }
    }

    /**
     * Maps this version to a descriptive string.
     *
     * @return A string describing this version.
     */
    @Override
    @Nonnull
    public String getDisplayName() {
      if (this.endOfSupport != null)
        return Messages.Downloads_Version_DisplayNameWithDate(this.name, this.status.getDisplayName(), this.endOfSupport);
      else
        return Messages.Downloads_Version_DisplayName(this.name, this.status.getDisplayName());
    }

    /**
     * Gets a release, via its name.
     *
     * @param name The name of the release.
     *
     * @return The release, or {@code null} if it was not found.
     */
    @CheckForNull
    public Release getRelease(@CheckForNull String name) {
      return this.releaseMap.get(name);
    }

  }

  //endregion

  //region Release

  /** A .NET release. */
  public static final class Release implements ModelObject {

    /** The name of the release. */
    public String name;

    /** The date of release. */
    public String released;

    /** Indicates whether or not this release is a preview. */
    public boolean preview;

    /** Indicates whether or not this release contains security fixes. */
    public boolean securityFixes;

    /** A link to the release notes for this release. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String releaseNotes;

    /** The SDKs included in this release. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public List<String> sdks;

    private void finish() {
      if (this.name == null)
        this.name = Messages.Downloads_Unknown();
      if (this.released == null)
        this.released = Messages.Downloads_Unknown();
      if (this.sdks == null)
        this.sdks = Collections.emptyList();
    }

    /**
     * Maps this release to a descriptive string.
     *
     * @return A string describing this release.
     */
    @Override
    @Nonnull
    public String getDisplayName() {
      if (this.securityFixes)
        return Messages.Downloads_Release_DisplayNameWithSecurity(this.name, this.released);
      return Messages.Downloads_Release_DisplayName(this.name, this.released);
    }

    /**
     * Gets markup linking to the release notes for this release, if they are available.
     *
     * @return Markup linking to the release notes for this release, or {@code null} if they are not available.
     */
    @CheckForNull
    public String getReleaseNotesLink() {
      if (this.releaseNotes == null)
        return null;
      return Messages.Downloads_Release_ReleaseNotesLink(this.releaseNotes);
    }

  }

  //endregion

  //region Sdk

  /** A .NET SDK. */
  public static final class Sdk implements ModelObject {

    /** The name of the SDK. */
    public String name;

    /** Information about the SDK (such as the version of Visual Studio that includes tooling for it). */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String info;

    /** The common prefix for the download links of the packages associated with this SDK. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String urlPrefix;

    /** This SDK's packages. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public Package[] packages;

    private final Map<String, Package> packageMap = new HashMap<>();

    private void finish() {
      if (this.name == null)
        this.name = Messages.Downloads_Unknown();
      if (!this.packageMap.isEmpty())
        this.packageMap.clear();
      if (this.packages == null)
        return;
      for (final Package p : this.packages) {
        p.finish(this.urlPrefix);
        this.packageMap.put(p.url, p);
      }
    }

    /**
     * Maps this SDK to a descriptive string.
     *
     * @return A string describing this SDK.
     */
    @Override
    @Nonnull
    public String getDisplayName() {
      return Messages.Downloads_Sdk_DisplayName(this.name);
    }

    /**
     * Gets a package for this SDK, via its download link.
     *
     * @param url The download link for the package.
     *
     * @return The requested package, or {@code null} if it was not found.
     */
    public Package getPackage(String url) {
      return this.packageMap.get(url);
    }

  }

  //endregion

  //region Package

  /** A .NET package. */
  public static final class Package implements ModelObject {

    /** The RID (runtime identifier) of the platform for which this package is intended. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String rid;

    /** A string describing the platform for which this package is intended. */
    public String platform;

    /** The hash (SHA256) of the package. */
    public String hash;

    /** The download URL for the package. */
    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String url;

    private void finish(String urlPrefix) {
      if (this.hash == null)
        this.hash = Messages.Downloads_Unknown();
      if (this.platform == null)
        this.platform = Messages.Downloads_Unknown();
      if (urlPrefix != null && this.url != null)
        this.url = urlPrefix + this.url;
    }

    /**
     * Maps this package to a descriptive string.
     *
     * @return A string describing this package.
     */
    @Override
    @Nonnull
    public String getDisplayName() {
      return Messages.Downloads_Package_DisplayName(this.rid, this.platform);
    }

    /**
     * Gets markup linking to this package.
     *
     * @return Markup linking to this package.
     */
    @Nonnull
    public String getDirectDownloadLink() {
      // For now, this does not include the file hash (it's a bit long)
      return Messages.Downloads_Package_DirectDownloadLink(this.url);
    }

  }

  //endregion

  //endregion

  //region Internals

  private Map<String, Sdk> sdks;

  private Map<String, Version> versions;

  /**
   * Gets the (single) instance of {@link Downloads}.
   *
   * @return An instance of {@link Downloads}, loaded with all available SDK installation packages for all .NET versions/releases.
   */
  @Nonnull
  public static synchronized Downloads getInstance() {
    // JENKINS-62572: would be simpler to pass just the class
    final DownloadService.Downloadable instance = DownloadService.Downloadable.get(Downloads.class.getName().replace('$', '.'));
    if (instance instanceof Downloads)
      return ((Downloads) instance).loadData();
    else { // No such downloadable (should be impossible).
      final Downloads empty = new Downloads();
      empty.sdks = Collections.emptyMap();
      empty.versions = Collections.emptyMap();
      return empty;
    }
  }

  @Nonnull
  private Downloads loadData() {
    if (this.sdks != null && this.versions != null)
      return this;
    try {
      final JSONObject json = this.getData();
      if (json != null) {
        // TODO: Use custom deserialization
        this.sdks = new LinkedHashMap<>();
        for (Object item : json.getJSONArray("sdks")) {
          if (item instanceof JSONObject) {
            final JSONObject jobj = (JSONObject) item;
            if (!jobj.isNullObject() && !jobj.isArray()) {
              final Sdk sdk = (Sdk) JSONObject.toBean(jobj, Sdk.class);
              sdk.finish();
              this.sdks.put(sdk.name, sdk);
            }
          }
        }
        this.versions = new LinkedHashMap<>();
        { // Explicit morpher fiddling to avoid a warning being logged
          final Morpher statusMorpher = new EnumMorpher(Version.Status.class);
          JSONUtils.getMorpherRegistry().registerMorpher(statusMorpher);
          try {
            for (Object item : json.getJSONArray("versions")) {
              if (item instanceof JSONObject) {
                final JSONObject jobj = (JSONObject) item;
                if (!jobj.isNullObject() && !jobj.isArray()) {
                  final Version v = (Version) JSONObject.toBean(jobj, Version.class);
                  v.finish();
                  this.versions.put(v.name, v);
                }
              }
            }
          }
          finally {
            JSONUtils.getMorpherRegistry().deregisterMorpher(statusMorpher);
          }
        }
      }
    }
    catch (Throwable t) {
      Downloads.LOGGER.log(Level.FINE, Messages.Framework_LoadFailed(), t);
    }
    finally {
      if (this.sdks == null)
        this.sdks = Collections.emptyMap();
      if (this.sdks.isEmpty())
        Downloads.LOGGER.fine(Messages.Downloads_NoSdks());
      if (this.versions == null)
        this.versions = Collections.emptyMap();
      if (this.versions.isEmpty())
        Downloads.LOGGER.fine(Messages.Downloads_NoVersions());
    }
    return this;
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Downloads.class.getName());

  //endregion

}
