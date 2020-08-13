package io.jenkins.plugins.dotnet.commands.nuget;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class NuGetCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(NuGetCommand::new, check -> check.expectCommand().withArgument("nuget"));
  }

}
