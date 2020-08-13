package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

public final class BasicCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandAndValidateProcessExecution(Command::new, CommandLineChecker::expectCommand);
  }

  @Test
  public void showSdkInfoFlagWorks() throws Exception {
    this.runCommandAndValidateProcessExecution(() -> {
      final Command command = new Command();
      command.setShowSdkInfo(true);
      return command;
    }, check -> {
      check.expectCommand().withArgument("--info");
      check.expectCommand();
    });
    this.runCommandAndValidateProcessExecution(() -> {
      final Command command = new Command();
      command.setShowSdkInfo(false);
      return command;
    }, CommandLineChecker::expectCommand);
  }

}
