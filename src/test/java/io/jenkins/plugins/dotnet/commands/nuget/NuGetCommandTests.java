package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class NuGetCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(NuGetCommand::new, check -> check.expectCommand().withArgument("nuget"));
  }

  @Test
  public void forceEnglishOutputFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final NuGetCommand command = new NuGetCommand();
      command.setForceEnglishOutput(true);
      return command;
    }, check -> check.expectCommand().withArguments("nuget", "--force-english-output"));
    super.runCommandAndValidateProcessExecution(() -> {
      final NuGetCommand command = new NuGetCommand();
      command.setForceEnglishOutput(false);
      return command;
    }, check -> check.expectCommand().withArguments("nuget"));
  }

}
