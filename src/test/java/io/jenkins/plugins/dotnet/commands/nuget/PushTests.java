package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class PushTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Push::new, check -> check.expectCommand().withArguments("nuget", "push"));
  }

  private static final String ROOT = "path/to/Some.Package.42.666.nupkg";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Push command = new Push();
      command.setRoot(PushTests.ROOT);
      return command;
    }, check -> check.expectCommand().withArguments("nuget", "push", PushTests.ROOT));
  }

}
