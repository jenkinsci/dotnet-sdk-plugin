package io.jenkins.plugins.dotnet.data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Util;
import hudson.model.ModelObject;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.Messages;
import net.sf.ezmorph.Morpher;
import net.sf.json.util.EnumMorpher;
import net.sf.json.util.JSONUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

  public Sdk[] sdks = null;

  private final Map<String, Sdk> sdkMap = new HashMap<>();

  public Version[] versions = null;

  private final Map<String, Version> versionMap = new HashMap<>();

  private void finish() {
    if (!this.sdkMap.isEmpty())
      this.sdkMap.clear();
    if (this.sdks != null) {
      for (final Sdk s : this.sdks) {
        s.finish();
        this.sdkMap.put(s.name, s);
      }
    }
    if (!this.versionMap.isEmpty())
      this.versionMap.clear();
    if (this.versions != null) {
      for (final Version v : this.versions) {
        v.finish();
        this.versionMap.put(v.name, v);
      }
    }
  }

  //region Lookup Methods

  private static Package getPackage(Sdk s, String url) {
    if (s == null)
      return null;
    return s.getPackage(url);
  }

  public Package getPackage(String sdk, String url) {
    return Downloads.getPackage(this.getSdk(sdk), url);
  }

  public Package getPackage(String version, String release, String sdk, String url) {
    return Downloads.getPackage(this.getSdk(version, release, sdk), url);
  }

  public Release getRelease(String version, String name) {
    final Version v = this.getVersion(version);
    if (v == null)
      return null;
    return v.getRelease(name);
  }

  public Sdk getSdk(String name) {
    return this.sdkMap.get(name);
  }

  public Sdk getSdk(String version, String release, String name) {
    final Release r = this.getRelease(version, release);
    if (r == null || r.sdks == null || !r.sdks.contains(name))
      return null;
    return this.getSdk(name);
  }

  public Version getVersion(String name) {
    return this.versionMap.get(name);
  }

  //endregion

  //region Listbox Methods

  public ListBoxModel addPackages(@Nonnull ListBoxModel model, String sdk) {
    final Sdk s = this.getSdk(sdk);
    if (s != null && s.packages != null) {
      for (Package p : s.packages)
        model.add(p, p.url);
    }
    return model;
  }

  public ListBoxModel addReleases(@Nonnull ListBoxModel model, String version, boolean includePreview) {
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

  public ListBoxModel addSdks(@Nonnull ListBoxModel model, String version, String release) {
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

  public ListBoxModel addVersions(@Nonnull ListBoxModel model) {
    if (this.versions != null) {
      for (Version v : this.versions)
        model.add(v, v.name);
    }
    return model;
  }

  //endregion

  //region Nested Types

  //region Version

  public static final class Version implements ModelObject {

    //region Status Enum

    public enum Status {

      CURRENT,

      EOL,

      LTS,

      MAINTENANCE,

      PREVIEW,

      UNKNOWN,

      ;

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

  public static final class Release implements ModelObject {

    public String name;

    public String released;

    public boolean preview;

    public boolean securityFixes;

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

    @Nonnull
    public String getDisplayName() {
      if (this.securityFixes)
        return Messages.Downloads_Release_DisplayNameWithSecurity(this.name, this.released);
      return Messages.Downloads_Release_DisplayName(this.name, this.released);
    }

  }

  //endregion

  //region Sdk

  public static final class Sdk implements ModelObject {

    public String name;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String info;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String urlPrefix;

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

    @Nonnull
    public String getDisplayName() {
      return Messages.Downloads_Sdk_DisplayName(this.name);
    }

    public Package getPackage(String url) {
      return this.packageMap.get(url);
    }

  }

  //endregion

  //region Package

  public static final class Package implements ModelObject {

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String rid;

    public String platform;

    @SuppressFBWarnings(
      value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = "Set by JSON deserialization."
    )
    public String url;

    private void finish(String urlPrefix) {
      if (this.platform == null)
        this.platform = Messages.Downloads_Unknown();
      if (urlPrefix != null && this.url != null)
        this.url = urlPrefix + this.url;
    }

    @Nonnull
    public String getDisplayName() {
      return Messages.Downloads_Package_DisplayName(this.rid, this.platform);
    }

  }

  //endregion

  //endregion

}
