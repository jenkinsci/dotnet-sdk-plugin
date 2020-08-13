package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

public final class RestoreTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Restore::new, check -> check.expectCommand().withArgument("restore"));
  }

}
