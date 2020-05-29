package io.jenkins.plugins.dotnet.data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.ezmorph.Morpher;
import net.sf.json.util.EnumMorpher;
import net.sf.json.util.JSONUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class Downloads {

  private static Downloads instance = null;

  public static synchronized Downloads getInstance() {
    if (Downloads.instance == null) {
      { // Explicit morpher fiddling to avoid a warning being logged
        final Morpher statusMorpher = new EnumMorpher(Version.Status.class);
        JSONUtils.getMorpherRegistry().registerMorpher(statusMorpher);
        Downloads.instance = Data.loadJson(Downloads.class, Downloads.class);
        JSONUtils.getMorpherRegistry().deregisterMorpher(statusMorpher);
      }
      if (Downloads.instance == null)
        Downloads.instance = new Downloads();
      else
        Downloads.instance.finish();
    }
    return Downloads.instance;
  }

  public Version[] versions = null;

  private final Map<String, Version> versionMap = new HashMap<>();

  private void finish() {
    if (!this.versionMap.isEmpty())
      this.versionMap.clear();
    if (this.versions == null)
      return;
    for (final Version v : this.versions) {
      v.finish();
      this.versionMap.put(v.name, v);
    }
  }

  //region Lookup Methods

  public Package getPackage(String version, String release, String sdk, String url) {
    final Version v = this.getVersion(version);
    if (v == null)
      return null;
    final Release r = v.getRelease(release);
    if (r == null)
      return null;
    final Sdk s = r.getSdk(sdk);
    if (s == null)
      return null;
    return s.getPackage(url);
  }

  public Release getRelease(String version, String release) {
    final Version v = this.getVersion(version);
    if (v == null)
      return null;
    return v.getRelease(release);
  }

  public Sdk getSdk(String version, String release, String sdk) {
    final Version v = this.getVersion(version);
    if (v == null)
      return null;
    final Release r = v.getRelease(release);
    if (r == null)
      return null;
    return r.getSdk(sdk);
  }

  public Version getVersion(String name) {
    return this.versionMap.get(name);
  }

  //endregion

  //region Listbox Methods

  public ListBoxModel addPackages(@Nonnull ListBoxModel model, String version, String release, String sdk) {
    final Sdk s = this.getSdk(version, release, sdk);
    if (s != null && s.packages != null) {
      for (Package p : s.packages)
        model.add(p.getDisplayName(), p.url);
    }
    return model;
  }

  public ListBoxModel addReleases(@Nonnull ListBoxModel model, String version, boolean includePreview) {
    final Version v = this.getVersion(version);
    if (v != null && v.releases != null) {
      for (Release r : v.releases) {
        if (r.preview && !includePreview)
          continue;
        model.add(r.getDisplayName(), r.name);
      }
    }
    return model;
  }

  public ListBoxModel addSdks(@Nonnull ListBoxModel model, String version, String release) {
    final Release r = this.getRelease(version, release);
    if (r != null && r.sdks != null) {
      for (Sdk s : r.sdks)
        model.add(s.getDisplayName(), s.name);
    }
    return model;
  }

  public ListBoxModel addVersions(@Nonnull ListBoxModel model, boolean includePreview, boolean includeEndOfLife) {
    if (this.versions != null) {
      for (Version v : this.versions) {
        if (v.status == Version.Status.PREVIEW && !includePreview)
          continue;
        if (v.status == Version.Status.END_OF_LIFE && !includeEndOfLife)
          continue;
        model.add(v.getDisplayName(), v.name);
      }
    }
    return model;
  }

  //endregion

  //region Nested Types

  //region Version

  public static final class Version {

    //region Status Enum

    public enum Status {

      CURRENT,

      END_OF_LIFE,

      LTS,

      MAINTENANCE,

      PREVIEW,

      UNKNOWN,

      ;

      public String getDisplayName() {
        switch (this) {
          case CURRENT:
            return Messages.Downloads_Version_Status_Current();
          case END_OF_LIFE:
            return Messages.Downloads_Version_Status_EndOfLife();
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

    public String name;

    public Status status;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String endOfSupport;

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

    @Nonnull
    public String getDisplayName() {
      if (this.endOfSupport != null)
        return Messages.Downloads_Version_DisplayNameWithDate(this.name, this.status.getDisplayName(), this.endOfSupport);
      else
        return Messages.Downloads_Version_DisplayName(this.name, this.status.getDisplayName());
    }

    public Release getRelease(String name) {
      return this.releaseMap.get(name);
    }

  }

  //endregion

  //region Release

  public static final class Release {

    public String name;

    public String released;

    public boolean preview;

    public boolean securityFixes;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public Sdk[] sdks;

    private final Map<String, Sdk> sdkMap = new HashMap<>();

    private void finish() {
      if (this.name == null)
        this.name = Messages.Downloads_Unknown();
      if (this.released == null)
        this.released = Messages.Downloads_Unknown();
      if (!this.sdkMap.isEmpty())
        this.sdkMap.clear();
      if (this.sdks == null)
        return;
      for (final Sdk s : this.sdks) {
        s.finish();
        this.sdkMap.put(s.name, s);
      }
    }

    @Nonnull
    public String getDisplayName() {
      if (this.securityFixes)
        return Messages.Downloads_Release_DisplayNameWithSecurity(this.name, this.released);
      return Messages.Downloads_Release_DisplayName(this.name, this.released);
    }

    public Sdk getSdk(String name) {
      return this.sdkMap.get(name);
    }

  }

  //endregion

  //region Sdk

  public static final class Sdk {

    public String name;

    public String vs;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public Package[] packages;

    private final Map<String, Package> packageMap = new HashMap<>();

    private void finish() {
      if (this.name == null)
        this.name = Messages.Downloads_Unknown();
      if (this.vs == null)
        this.vs = Messages.Downloads_Unknown();
      if (!this.packageMap.isEmpty())
        this.packageMap.clear();
      if (this.packages == null)
        return;
      for (final Package p : this.packages) {
        p.finish();
        this.packageMap.put(p.url, p);
      }
    }

    @Nonnull
    public String getDisplayName() {
      return Messages.Downloads_Sdk_DisplayName(this.name, this.vs);
    }

    public Package getPackage(String url) {
      return this.packageMap.get(url);
    }

  }

  //endregion

  //region Package

  public static final class Package {

    public String platform;

    public String architecture;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String url;

    private void finish() {
      if (this.platform == null)
        this.platform = Messages.Downloads_Unknown();
      if (this.architecture == null)
        this.architecture = Messages.Downloads_Unknown();
    }

    @Nonnull
    public String getDisplayName() {
      return Messages.Downloads_Package_DisplayName(this.platform, this.architecture);
    }

  }

  //endregion

  //endregion

}
