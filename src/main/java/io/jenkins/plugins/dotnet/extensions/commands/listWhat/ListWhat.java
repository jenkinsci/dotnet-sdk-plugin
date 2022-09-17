package io.jenkins.plugins.dotnet.extensions.commands.listWhat;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import io.jenkins.plugins.dotnet.extensions.commands.CommandLineArgumentProvider;

import java.io.Serializable;

public abstract class ListWhat extends AbstractDescribableImpl<ListWhat>
  implements CommandLineArgumentProvider, ExtensionPoint, Serializable {

  private static final long serialVersionUID = -2294720457629616913L;

}
