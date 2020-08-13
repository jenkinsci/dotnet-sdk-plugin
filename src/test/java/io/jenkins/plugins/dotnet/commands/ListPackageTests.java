package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

public final class ListPackageTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(ListPackage::new, check -> check.expectCommand().withArguments("list", "package"));
  }

}
