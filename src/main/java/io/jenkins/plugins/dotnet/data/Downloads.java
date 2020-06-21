package io.jenkins.plugins.dotnet.data;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.DownloadService;
import hudson.model.ModelObject;
import hudson.util.ListBoxModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
   * @param version The name of the version containing the release to which the package belongs.
   * @param release The name of the release containing the SDK to which the package belongs.
   * @param sdk     The name of the SDK to which the package belongs.
   * @param url     The download link for the package.
   *
   * @return The requested package, or {@code null} if it was not found.
   */
  @CheckForNull
  public Package getPackage(@CheckForNull String version, @CheckForNull String release, @CheckForNull String sdk, @CheckForNull String url) {
    final Sdk s = this.getSdk(version, release, sdk);
    if (s == null)
      return null;
    return s.getPackage(url);
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
    if (r == null || !r.sdks.contains(name))
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
  @NonNull
  public ListBoxModel addPackages(@NonNull ListBoxModel model, @CheckForNull String sdk) {
    final Sdk s = this.getSdk(sdk);
    if (s != null) {
      for (Package p : s.packages.values())
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
  @NonNull
  public ListBoxModel addReleases(@NonNull ListBoxModel model, @CheckForNull String version, boolean includePreview) {
    final Version v = this.getVersion(version);
    if (v != null) {
      for (Release r : v.releases.values()) {
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
  @NonNull
  public ListBoxModel addSdks(@NonNull ListBoxModel model, @CheckForNull String version, @CheckForNull String release) {
    final Release r = this.getRelease(version, release);
    if (r != null) {
      for (final String sdk : r.sdks) {
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
  @NonNull
  public ListBoxModel addVersions(@NonNull ListBoxModel model) {
    for (Version v : this.versions.values())
      model.add(v, v.name);
    return model;
  }

  //endregion

  //region Nested Types

  //region Version

  /** A .NET version. */
  public static final class Version implements ModelObject {

    Version(@NonNull JSONObject json) {
      {
        final Object value = json.get("name");
        if (value instanceof String)
          this.name = (String) value;
        else
          throw new JSONException("Version object lacks 'name' property.");
      }
      {
        final Object value = json.get("status");
        if (value instanceof String)
          this.status = Enum.valueOf(Status.class, (String) value);
        else
          this.status = Status.UNKNOWN;
      }
      {
        final Object value = json.get("endOfSupport");
        if (value instanceof String)
          this.endOfSupport = (String) value;
        else
          this.endOfSupport = null;
      }
      this.releases = Downloads.readJsonObjectArray(json, "releases", Release::new, r -> r.name);
    }

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
      @NonNull
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
          default:
            return this.toString();
        }
      }

    }

    //endregion

    /** The name of the version. */
    @NonNull
    public final String name;

    /** The status of the version. */
    @NonNull
    public final Status status;

    /** The date on which support for this version ends (or ended), if known. */
    @CheckForNull
    public String endOfSupport;

    /** The releases for this version. */
    @NonNull
    private final Map<String, Release> releases;

    /**
     * Maps this version to a descriptive string.
     *
     * @return A string describing this version.
     */
    @Override
    @NonNull
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
      return this.releases.get(name);
    }

    /**
     * Gets all releases for this version.
     *
     * @return All releases for this version.
     */
    @NonNull
    public Collection<Release> getReleases() {
      if (this.releases.isEmpty())
        return Collections.emptyList();
      return this.releases.values();
    }

  }

  //endregion

  //region Release

  /** A .NET release. */
  public static final class Release implements ModelObject {

    Release(@NonNull JSONObject json) {
      {
        final Object value = json.get("name");
        if (value instanceof String)
          this.name = (String) value;
        else
          throw new JSONException("Release object lacks 'name' property.");
      }
      {
        final Object value = json.get("released");
        if (value instanceof String)
          this.released = (String) value;
        else
          this.released = Messages.Downloads_Unknown();
      }
      {
        final Object value = json.get("preview");
        if (value instanceof Boolean)
          this.preview = (boolean) value;
        else
          this.preview = false;
      }
      {
        final Object value = json.get("securityFixes");
        if (value instanceof Boolean)
          this.securityFixes = (boolean) value;
        else
          this.securityFixes = false;
      }
      {
        final Object value = json.get("releaseNotes");
        if (value instanceof String)
          this.releaseNotes = (String) value;
        else
          this.releaseNotes = null;
      }
      this.sdks = Collections.unmodifiableList(Downloads.readJsonStringArray(json, "sdks"));
    }

    /** The name of the release. */
    @NonNull
    public String name;

    /** The date of release. */
    @NonNull
    public String released;

    /** Indicates whether or not this release is a preview. */
    public boolean preview;

    /** Indicates whether or not this release contains security fixes. */
    public boolean securityFixes;

    /** A link to the release notes for this release. */
    @CheckForNull
    public String releaseNotes;

    /** The SDKs included in this release. */
    @NonNull
    public List<String> sdks;

    /**
     * Maps this release to a descriptive string.
     *
     * @return A string describing this release.
     */
    @Override
    @NonNull
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

    Sdk(@NonNull JSONObject json) {
      {
        final Object value = json.get("name");
        if (value instanceof String)
          this.name = (String) value;
        else
          throw new JSONException("SDK object lacks 'name' property.");
      }
      {
        final Object value = json.get("info");
        if (value instanceof String)
          this.info = (String) value;
        else
          this.info = null;
      }
      final String urlPrefix;
      {
        final Object value = json.get("urlPrefix");
        if (value instanceof String)
          urlPrefix = (String) value;
        else
          urlPrefix = null;
      }
      this.packages = Downloads.readJsonObjectArray(json, "packages", j -> new Package(j, urlPrefix), p -> p.url);
    }

    /** The name of the SDK. */
    @NonNull
    public final String name;

    /** Information about the SDK (such as the version of Visual Studio that includes tooling for it). */
    @CheckForNull
    public final String info;

    @NonNull
    private final Map<String, Package> packages;

    /**
     * Maps this SDK to a descriptive string.
     *
     * @return A string describing this SDK.
     */
    @Override
    @NonNull
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
    @CheckForNull
    public Package getPackage(@CheckForNull String url) {
      return this.packages.get(url);
    }

    /**
     * Gets all packages for this SDK.
     *
     * @return All packages for this SDK.
     */
    @NonNull
    public Collection<Package> getPackages() {
      if (this.packages.isEmpty())
        return Collections.emptyList();
      return this.packages.values();
    }

  }

  //endregion

  //region Package

  /** A .NET package. */
  public static final class Package implements ModelObject {

    Package(@NonNull JSONObject json, @CheckForNull String urlPrefix) {
      {
        final Object value = json.get("rid");
        if (value instanceof String)
          this.rid = (String) value;
        else
          this.rid = Messages.Downloads_Unknown();
      }
      {
        final Object value = json.get("platform");
        if (value instanceof String)
          this.platform = (String) value;
        else
          this.platform = Messages.Downloads_Unknown();
      }
      {
        final Object value = json.get("url");
        if (value instanceof String) {
          String text = (String) value;
          if (urlPrefix != null)
            text = urlPrefix + text;
          this.url = text;
        }
        else
          throw new JSONException("Package object lacks 'url' property.");
      }
    }

    /** The RID (runtime identifier) of the platform for which this package is intended. */
    @NonNull
    public final String rid;

    /** A string describing the platform for which this package is intended. */
    @NonNull
    public final String platform;

    /** The download URL for the package. */
    @NonNull
    public final String url;

    /**
     * Maps this package to a descriptive string.
     *
     * @return A string describing this package.
     */
    @Override
    @NonNull
    public String getDisplayName() {
      return Messages.Downloads_Package_DisplayName(this.rid, this.platform);
    }

    /**
     * Gets markup linking to this package.
     *
     * @return Markup linking to this package.
     */
    @NonNull
    public String getDirectDownloadLink() {
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
  @NonNull
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

  /**
   * Gets all known .NET SDKs.
   *
   * @return All known .NET SDKs.
   */
  @NonNull
  public Collection<Sdk> getSdks() {
    if (this.sdks == null || this.sdks.isEmpty())
      return Collections.emptyList();
    return this.sdks.values();
  }

  /**
   * Gets all known .NET versions.
   *
   * @return All known .NET versions.
   */
  @NonNull
  public Collection<Version> getVersions() {
    if (this.versions == null || this.versions.isEmpty())
      return Collections.emptyList();
    return this.versions.values();
  }

  @NonNull
  private Downloads loadData() {
    if (this.sdks != null && this.versions != null)
      return this;
    try {
      final JSONObject json = this.getData();
      if (json != null) {
        this.sdks = Downloads.readJsonObjectArray(json, "sdks", Sdk::new, sdk -> sdk.name);
        this.versions = Downloads.readJsonObjectArray(json, "versions", Version::new, v -> v.name);
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

  @NonNull
  public static <T> Map<String, T> readJsonObjectArray(@NonNull JSONObject o, @NonNull String prop, @NonNull Function<JSONObject, T> convert, @NonNull Function<T, String> createKey) {
    final Map<String, T> map = new LinkedHashMap<>();
    final Object array = o.get(prop);
    if (array instanceof JSONArray) {
      int idx = 0;
      for (Object item : (JSONArray) array) {
        ++idx;
        JSONObject jitem = null;
        if (item instanceof JSONObject) {
          jitem = (JSONObject) item;
          if (jitem.isNullObject() || jitem.isArray())
            jitem = null;
        }
        if (jitem != null) {
          final T t = convert.apply(jitem);
          map.put(createKey.apply(t), t);
        }
        else
          Downloads.LOGGER.warning(String.format("Element #%d of JSON array '%s' was not an object.", idx, prop));
      }
    }
    else
      Downloads.LOGGER.warning(String.format("The value of JSON property '%s' was not an array.", prop));
    return map;
  }

  @NonNull
  public static <T> List<String> readJsonStringArray(@NonNull JSONObject o, @NonNull String prop) {
    final List<String> list = new ArrayList<>();
    final Object array = o.get(prop);
    if (array instanceof JSONArray) {
      int idx = 0;
      for (Object item : (JSONArray) array) {
        ++idx;
        if (item instanceof String)
          list.add((String) item);
        else
          Downloads.LOGGER.warning(String.format("Element #%d of JSON array '%s' was not a string.", idx, prop));
      }
    }
    else
      Downloads.LOGGER.warning(String.format("The value of JSON property '%s' was not an array.", prop));
    return list;
  }

  /** A logger to use for trace messages. */
  private static final Logger LOGGER = Logger.getLogger(Downloads.class.getName());

  //endregion

}
