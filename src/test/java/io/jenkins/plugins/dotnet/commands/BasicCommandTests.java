package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

public final class BasicCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Command());
      clc.expectCommand();
    });
  }

  @Test
  public void showSdkInfoFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Command command = new Command();
        command.setShowSdkInfo(true);
        steps.add(command);
        clc.expectCommand().withArgument("--info");
        clc.expectCommand();
      }
      {
        final Command command = new Command();
        command.setShowSdkInfo(false);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

}
