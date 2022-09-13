package io.jenkins.plugins.dotnet.extensions.commands;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;

import java.io.Serializable;

public abstract class Command extends AbstractDescribableImpl<Command>
  implements CommandLineArgumentProvider, ExtensionPoint, Serializable {

  private static final long serialVersionUID = 9086963597760055328L;

}
