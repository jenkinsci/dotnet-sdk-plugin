package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class DeleteTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Delete());
      clc.expectCommand().withArguments("nuget", "delete");
    });
  }

  private static final String PACKAGE_ID = "My.Package";
  private static final String PACKAGE_VERSION = "42.666";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Delete command = new Delete();
      // package id and version both have to be specified
      command.setPackageName(DeleteTests.PACKAGE_ID);
      command.setPackageVersion(DeleteTests.PACKAGE_VERSION);
      steps.add(command);
      clc.expectCommand().withArguments("nuget", "delete", DeleteTests.PACKAGE_ID, DeleteTests.PACKAGE_VERSION);
    });
  }

}
