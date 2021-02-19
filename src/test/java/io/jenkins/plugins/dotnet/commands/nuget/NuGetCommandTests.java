package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class NuGetCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new NuGetCommand());
      clc.expectCommand().withArguments("nuget");
    });
  }

  @Test
  public void forceEnglishOutputFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final NuGetCommand command = new NuGetCommand();
        command.setForceEnglishOutput(true);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "--force-english-output");
      }
      {
        final NuGetCommand command = new NuGetCommand();
        command.setForceEnglishOutput(false);
        steps.add(command);
        clc.expectCommand().withArguments("nuget");
      }
    });
  }

}
