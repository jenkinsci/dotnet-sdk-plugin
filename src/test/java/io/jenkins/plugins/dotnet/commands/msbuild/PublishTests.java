package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class PublishTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Publish::new, check -> check.expectCommand().withArgument("publish"));
  }

}
