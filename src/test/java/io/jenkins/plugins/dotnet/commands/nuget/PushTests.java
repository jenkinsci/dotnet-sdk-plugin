package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public class PushTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Push::new, check -> check.expectCommand().withArguments("nuget", "push"));
  }

}
