package io.jenkins.plugins.dotnet.extensions.commands.nugetActions;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dotnet.DotNetArguments;
import io.jenkins.plugins.dotnet.extensions.commands.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.verb.POST;

public class Locals extends NuGetAction {

  private static final long serialVersionUID = 8214605511177927474L;

  @DataBoundConstructor
  public Locals() {
    this.cacheLocation = SpecialValues.defaultLocation();
    this.operation = SpecialValues.defaultOperation();
  }

  //region Special Values

  private static final String LOCATION_ALL = "all";

  private static final String LOCATION_GLOBAL_PACKAGES = "global-packages";

  private static final String LOCATION_HTTP_CACHE = "http-cache";

  private static final String LOCATION_TEMP = "temp";

  private static final String OPERATION_CLEAR = "clear";

  private static final String OPERATION_LIST = "list";

  public interface SpecialValues {

    @NonNull
    static String checkCacheLocation(@NonNull String cacheLocation) {
      cacheLocation = cacheLocation.trim().toLowerCase();
      switch (cacheLocation) {
        case Locals.LOCATION_ALL:
        case Locals.LOCATION_GLOBAL_PACKAGES:
        case Locals.LOCATION_HTTP_CACHE:
        case Locals.LOCATION_TEMP:
          return cacheLocation;
        default:
          throw new IllegalArgumentException(Messages.NuGet_Locals_InvalidCacheLocation(cacheLocation));
      }
    }

    @NonNull
    static String checkOperation(@NonNull String operation) {
      operation = operation.trim().toLowerCase();
      switch (operation) {
        case "clear":
        case "list":
          return operation;
        default:
          throw new IllegalArgumentException(Messages.NuGet_Locals_InvalidOperation(operation));
      }
    }

    @NonNull
    static String defaultLocation() {
      return Locals.LOCATION_ALL;
    }

    @NonNull
    static String defaultOperation() {
      return Locals.OPERATION_LIST;
    }

  }

  //endregion

  //region Properties

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
    this.cacheLocation = SpecialValues.checkCacheLocation(cacheLocation);
  }

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
    this.operation = SpecialValues.checkOperation(operation);
  }

  //endregion

  @Override
  public void addCommandLineArguments(@NonNull DotNetArguments arguments) throws AbortException {
    arguments.addOption("locals");
    super.addCommandLineArguments(arguments);
    arguments.add(this.cacheLocation);
    arguments.addFlag(this.operation, true);
  }

  public interface StaplerMethods {

    /**
     * Fills a listbox with the possible NuGet cache locations.
     *
     * @param item The item being configured.
     *
     * @return A suitably filled listbox model.
     */
    @NonNull
    @POST
    default ListBoxModel doFillCacheLocationItems(@CheckForNull @AncestorInPath Item item) {
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
    default ListBoxModel doFillOperationItems(@CheckForNull @AncestorInPath Item item) {
      if (item != null) {
        item.checkPermission(Item.CONFIGURE);
      }
      final ListBoxModel model = new ListBoxModel();
      model.add(Messages.NuGet_Locals_Operation_Clear(), Locals.OPERATION_CLEAR);
      model.add(Messages.NuGet_Locals_Operation_List(), Locals.OPERATION_LIST);
      return model;
    }

  }

  @Extension
  @Symbol("locals")
  public static final class DescriptorImpl extends NuGetActionDescriptor implements StaplerMethods {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.NuGet_Locals_DisplayName();
    }

  }

}
