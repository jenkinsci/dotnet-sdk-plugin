package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class DeleteTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Delete::new, check -> check.expectCommand().withArguments("nuget", "delete"));
  }

  private static final String PACKAGE_ID = "My.Package";
  private static final String PACKAGE_VERSION = "42.666";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Delete command = new Delete();
      // package id and version both have to be specified
      command.setPackageName(DeleteTests.PACKAGE_ID);
      command.setPackageVersion(DeleteTests.PACKAGE_VERSION);
      return command;
    }, check -> check.expectCommand().withArguments("nuget", "delete", DeleteTests.PACKAGE_ID, DeleteTests.PACKAGE_VERSION));
  }

}
