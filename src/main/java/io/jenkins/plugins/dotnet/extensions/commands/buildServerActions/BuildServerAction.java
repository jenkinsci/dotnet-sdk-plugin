package io.jenkins.plugins.dotnet.extensions.commands.buildServerActions;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import io.jenkins.plugins.dotnet.extensions.commands.CommandLineArgumentProvider;

import java.io.Serializable;

public abstract class BuildServerAction extends AbstractDescribableImpl<BuildServerAction>
  implements CommandLineArgumentProvider, ExtensionPoint, Serializable {

  private static final long serialVersionUID = 5972566997120203240L;

}
