package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class MSBuildCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(MSBuildCommand::new, CommandLineChecker::expectCommand);
  }

  @Test
  public void shutDownBuildServersFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setShutDownBuildServers(true);
      return command;
    }, commandLineChecker -> {
      commandLineChecker.expectCommand();
      commandLineChecker.expectCommand().withArguments("build-server", "shutdown");
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setShutDownBuildServers(false);
      return command;
    }, CommandLineChecker::expectCommand);
  }

}
