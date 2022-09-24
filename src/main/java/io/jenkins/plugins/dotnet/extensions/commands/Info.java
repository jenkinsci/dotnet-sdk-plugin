package io.jenkins.plugins.dotnet.extensions.commands;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Item;
import hudson.util.FormValidation;
import io.jenkins.plugins.dotnet.DotNetArguments;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

public class Info extends Command {

  private static final long serialVersionUID = 4618580464187335911L;

  @DataBoundConstructor
  public Info() {
  }

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    if (this.listRuntimes) {
      arguments.addFlag("list-runtimes");
    }
    else if (this.listSdks) {
      arguments.addFlag("list-sdks");
    }
    else if (this.showVersion) {
      arguments.addFlag("version");
    }
    else {
      arguments.addFlag("info");
    }
  }

  //region Properties

  private boolean listRuntimes;

  public boolean isListRuntimes() {
    return this.listRuntimes;
  }

  @DataBoundSetter
  public void setListRuntimes(boolean listRuntimes) {
    this.listRuntimes = listRuntimes;
  }

  private boolean listSdks;

  public boolean isListSdks() {
    return this.listSdks;
  }

  @DataBoundSetter
  public void setListSdks(boolean listSdks) {
    this.listSdks = listSdks;
  }

  private boolean showVersion;

  public boolean isShowVersionOnly() {
    return this.showVersion;
  }

  @DataBoundSetter
  public void setShowVersionOnly(boolean showVersion) {
    this.showVersion = showVersion;
  }

  //endregion

  @Extension
  @Symbol("info")
  public static final class DescriptorImpl extends CommandDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.Info_DisplayName();
    }

    @NonNull
    @POST
    public FormValidation doCheckListRuntimes(@QueryParameter boolean listRuntimes, @QueryParameter boolean listSdks,
                                              @QueryParameter boolean showVersion, @CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      if (listRuntimes && (listSdks || showVersion)) {
        return FormValidation.error(Messages.Info_MutuallyExclusive());
      }
      return FormValidation.ok();
    }

    @NonNull
    @POST
    public FormValidation doCheckListSdks(@QueryParameter boolean listRuntimes, @QueryParameter boolean listSdks,
                                          @QueryParameter boolean showVersion, @CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      if (listSdks && (listRuntimes || showVersion)) {
        return FormValidation.error(Messages.Info_MutuallyExclusive());
      }
      return FormValidation.ok();
    }

    @NonNull
    @POST
    public FormValidation doCheckShowVersion(@QueryParameter boolean listRuntimes, @QueryParameter boolean listSdks,
                                             @QueryParameter boolean showVersion, @CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      if (showVersion && (listRuntimes || listSdks)) {
        return FormValidation.error(Messages.Info_MutuallyExclusive());
      }
      return FormValidation.ok();
    }

  }

}
