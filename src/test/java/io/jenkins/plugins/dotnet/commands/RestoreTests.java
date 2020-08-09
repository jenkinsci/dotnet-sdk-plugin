package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

public final class RestoreTests extends CommandTests {

  @Test
  public void simpleRestoreRunsCorrectCommand() throws Exception {
    super.runCommandAndValidateProcessExecution(Restore::new, check -> {
      check.expectCommand().withArgument("restore");
    });
  }

  @Test
  public void simpleRestoreWithInfoRunsCorrectCommands() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore restore = new Restore();
      restore.setShowSdkInfo(true);
      return restore;
    }, check -> {
      check.expectCommand().withArgument("--info");
      check.expectCommand().withArgument("restore");
    });
  }

}
