package io.jenkins.plugins.dotnet.commands.nuget;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.commands.DotNetArguments;
import io.jenkins.plugins.dotnet.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.verb.POST;

/** A build step to run "{@code dotnet nuget locals}", listing or clearing one or more local package stores. */
public final class Locals extends NuGetCommand {

  /** Creates a new "{@code dotnet nuget locals}" build step. */
  @DataBoundConstructor
  public Locals() {
    super("locals");
    this.cacheLocation = Locals.LOCATION_ALL;
    this.operation = Locals.OPERATION_LIST;
  }

  @Override
  protected void addCommandLineArguments(@NonNull DotNetArguments args) throws AbortException {
    super.addCommandLineArguments(args);
    args.add(this.cacheLocation);
    args.addFlag(this.operation, true);
  }

  //region Properties

  private static final String LOCATION_ALL = "all";

  private static final String LOCATION_GLOBAL_PACKAGES = "global-packages";

  private static final String LOCATION_HTTP_CACHE = "http-cache";

  private static final String LOCATION_TEMP = "temp";

  @NonNull
  private String cacheLocation;

  /**
   * Gets the cache location to operate on.
   * <p>
   * This can be one of:
   * <ul>
   *   <li>{@value LOCATION_GLOBAL_PACKAGES}: the global package cache</li>
   *   <li>{@value LOCATION_HTTP_CACHE}: the cache used for HTTP responses</li>
   *   <li>{@value LOCATION_TEMP}: temporary storage</li>
   *   <li>{@value LOCATION_ALL}: all of the above</li>
   * </ul>
   *
   * @return The cache location to operate on.
   */
  @NonNull
  public String getCacheLocation() {
    return this.cacheLocation;
  }

  /**
   * Sets the cache location to operate on.
   *
   * @param cacheLocation The cache location to operate on; this can be one of:
   *                      <ul>
   *                        <li>{@value LOCATION_GLOBAL_PACKAGES}: the global package cache</li>
   *                        <li>{@value LOCATION_HTTP_CACHE}: the cache used for HTTP responses</li>
   *                        <li>{@value LOCATION_TEMP}: temporary storage</li>
   *                        <li>{@value LOCATION_ALL}: all of the above</li>
   *                      </ul>
   */
  @DataBoundSetter
  public void setCacheLocation(@NonNull String cacheLocation) {
    cacheLocation = cacheLocation.trim().toLowerCase();
    switch (cacheLocation) {
      case Locals.LOCATION_ALL:
      case Locals.LOCATION_GLOBAL_PACKAGES:
      case Locals.LOCATION_HTTP_CACHE:
      case Locals.LOCATION_TEMP:
        this.cacheLocation = cacheLocation;
        break;
      default:
        throw new IllegalArgumentException(Messages.NuGet_Locals_InvalidCacheLocation(cacheLocation));
    }
  }

  private static final String OPERATION_CLEAR = "clear";

  private static final String OPERATION_LIST = "list";

  @NonNull
  private String operation;

  /**
   * Gets the operation to apply.
   * <p>
   * This can be one of:
   * <ul>
   *   <li>{@value OPERATION_CLEAR}: clears the contents of the cache location</li>
   *   <li>{@value OPERATION_LIST}: shows the path to the cache location</li>
   * </ul>
   *
   * @return The operation to apply.
   */
  @NonNull
  public String getOperation() {
    return this.operation;
  }

  /**
   * Sets the operation to apply.
   *
   * @param operation The operation to apply; this can be one of:
   *                  <ul>
   *                    <li>{@value OPERATION_CLEAR}: clears the contents of the cache location</li>
   *                    <li>{@value OPERATION_LIST}: shows the path to the cache location</li>
   *                  </ul>
   */
  @DataBoundSetter
  public void setOperation(@NonNull String operation) {
    operation = operation.trim().toLowerCase();
    switch (operation) {
      case "clear":
      case "list":
        this.operation = operation;
        break;
      default:
        throw new IllegalArgumentException(Messages.NuGet_Locals_InvalidOperation(operation));
    }
  }

  //endregion

  //region DescriptorImpl

  /** A descriptor for "{@code dotnet nuget locals}" build steps. */
  @Extension
  @Symbol("dotnetNuGetLocals")
  public static final class DescriptorImpl extends NuGetCommandDescriptor {

    /** Creates a new "{@code dotnet nuget locals}" build step descriptor instance. */
    public DescriptorImpl() {
      this.load();
    }

    /**
     * Fills a listbox with the possible NuGet cache locations.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    public ListBoxModel doFillCacheLocationItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.NuGet_Locals_Location_All(), Locals.LOCATION_ALL);
      model.add(Messages.NuGet_Locals_Location_GlobalPackages(), Locals.LOCATION_GLOBAL_PACKAGES);
      model.add(Messages.NuGet_Locals_Location_HttpCache(), Locals.LOCATION_HTTP_CACHE);
      model.add(Messages.NuGet_Locals_Location_Temp(), Locals.LOCATION_TEMP);
      return model;
    }

    /**
     * Fills a listbox with the possible operations that can be performed on NuGet cache locations.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    public ListBoxModel doFillOperationItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.NuGet_Locals_Operation_Clear(), Locals.OPERATION_CLEAR);
      model.add(Messages.NuGet_Locals_Operation_List(), Locals.OPERATION_LIST);
      return model;
    }

    /**
     * Gets the display name for this build step (as used in the project configuration UI).
     *
     * @return This build step's display name.
     */
    @NonNull
    public String getDisplayName() {
      return Messages.NuGet_Locals_DisplayName();
    }

  }

  //endregion

}
