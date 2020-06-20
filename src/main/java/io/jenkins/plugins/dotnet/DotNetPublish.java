package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import hudson.util.VariableResolver;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A build step using the 'dotnet' executable to publish a project. */
public class DotNetPublish extends DotNetUsingMSBuild {

  @DataBoundConstructor
  public DotNetPublish() {
  }

  @Override
  protected void addCommandLineArguments(@NonNull ArgumentListBuilder args, @NonNull VariableResolver<String> resolver, @NonNull Set<String> sensitive) {
    args.add("publish");
    super.addCommandLineArguments(args, resolver, sensitive);
    if (this.force)
      args.add("--force");
    if (this.framework != null)
      args.add("-f:" + this.framework);
    if (this.manifests != null) {
      for (final String manifest : this.manifests)
        args.add("--manifest", manifest);
    }
    if (this.noBuild)
      args.add("--no-build");
    if (this.noDependencies)
      args.add("--no-dependencies");
    if (this.noRestore)
      args.add("--no-restore");
    if (this.runtime != null)
      args.add("-r:" + this.runtime);
    if (this.selfContained != null)
      args.add("--self-contained", this.selfContained);
    if (this.versionSuffix != null)
      args.add("--version-suffix", this.versionSuffix);
  }

  //region Properties

  private boolean force;

  public boolean isForce() {
    return this.force;
  }

  @DataBoundSetter
  public void setForce(boolean force) {
    this.force = force;
  }

  private String framework;

  public String getFramework() {
    return this.framework;
  }

  @DataBoundSetter
  public void setFramework(String framework) {
    this.framework = Util.fixEmptyAndTrim(framework);
  }

  private String[] manifests;

  public String[] getManifests() {
    if (this.manifests == null)
      return null;
    return this.manifests.clone();
  }

  @DataBoundSetter
  public void setManifests(String[] manifests) {
    if (manifests != null) {
      final List<String> cleaned = new ArrayList<>();
      for (final String manifestLine : manifests) {
        if (manifestLine == null)
          continue;
        for (final String manifest : manifestLine.split("[\r\n]")) {
          final String clean = Util.fixEmptyAndTrim(manifest);
          if (clean != null)
            cleaned.add(clean);
        }
      }
      if (cleaned.isEmpty())
        this.manifests = null;
      else
        this.manifests = cleaned.toArray(new String[0]);
    }
    else
      this.manifests = null;
  }

  private boolean noBuild;

  public boolean isNoBuild() {
    return this.noBuild;
  }

  @DataBoundSetter
  public void setNoBuild(boolean noBuild) {
    this.noBuild = noBuild;
  }

  private boolean noDependencies;

  public boolean isNoDependencies() {
    return this.noDependencies;
  }

  @DataBoundSetter
  public void setNoDependencies(boolean noDependencies) {
    this.noDependencies = noDependencies;
  }

  private boolean noRestore;

  public boolean isNoRestore() {
    return this.noRestore;
  }

  @DataBoundSetter
  public void setNoRestore(boolean noRestore) {
    this.noRestore = noRestore;
  }

  private String runtime;

  public String getRuntime() {
    return this.runtime;
  }

  @DataBoundSetter
  public void setRuntime(String runtime) {
    this.runtime = Util.fixEmptyAndTrim(runtime);
  }

  private String selfContained;

  public String getSelfContained() {
    return this.selfContained;
  }

  @DataBoundSetter
  public void setSelfContained(String selfContained) {
    this.selfContained = Util.fixEmptyAndTrim(selfContained);
  }

  private String versionSuffix;

  public String getVersionSuffix() {
    return this.versionSuffix;
  }

  @DataBoundSetter
  public void setVersionSuffix(String versionSuffix) {
    this.versionSuffix = Util.fixEmptyAndTrim(versionSuffix);
  }

  //endregion

  //region DescriptorImpl

  @Extension
  @Symbol("dotnetPublish")
  public static class DescriptorImpl extends MSBuildCommandDescriptor {

    public DescriptorImpl() {
      this.load();
    }

    protected DescriptorImpl(Class<? extends DotNetPublish> clazz) {
      super(clazz);
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillSelfContainedItems() {
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.DotNetPublish_ProjectDefault(), null);
      model.add(Messages.DotNetPublish_Yes(), "true");
      model.add(Messages.DotNetPublish_No(), "false");
      return model;
    }

    @NonNull
    public String getDisplayName() {
      return Messages.DotNetPublish_DisplayName();
    }

  }

  //endregion

}
