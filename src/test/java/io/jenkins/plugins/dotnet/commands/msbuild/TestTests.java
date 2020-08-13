package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;

public final class TestTests extends CommandTests {

  @org.junit.Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Test::new, check -> check.expectCommand().withArgument("test"));
  }

}
