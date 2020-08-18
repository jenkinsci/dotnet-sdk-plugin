package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class CleanTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Clean::new, check -> check.expectCommand().withArgument("clean"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Clean command = new Clean();
      command.setProject(CleanTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("clean", CleanTests.PROJECT));
  }

  private static final String FRAMEWORK = "net5.0";

  @Test
  public void frameworkOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Clean command = new Clean();
      command.setFramework(CleanTests.FRAMEWORK);
      return command;
    }, check -> check.expectCommand().withArguments("clean", "-f:" + CleanTests.FRAMEWORK));
    super.runCommandAndValidateProcessExecution(() -> {
      final Clean command = new Clean();
      command.setFramework("  ");
      return command;
    }, check -> check.expectCommand().withArguments("clean"));
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Clean command = new Clean();
      command.setRuntime(CleanTests.RUNTIME);
      return command;
    }, check -> check.expectCommand().withArguments("clean", "-r:" + CleanTests.RUNTIME));
    super.runCommandAndValidateProcessExecution(() -> {
      final Clean command = new Clean();
      command.setRuntime(null);
      return command;
    }, check -> check.expectCommand().withArguments("clean"));
  }

}
