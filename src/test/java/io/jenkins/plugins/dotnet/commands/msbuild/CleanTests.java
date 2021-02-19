package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class CleanTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Clean());
      clc.expectCommand().withArguments("clean");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Clean command = new Clean();
      command.setProject(CleanTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("clean", CleanTests.PROJECT);
    });
  }

  private static final String FRAMEWORK = "net5.0";

  @Test
  public void frameworkOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Clean command = new Clean();
        command.setFramework(CleanTests.FRAMEWORK);
        steps.add(command);
        clc.expectCommand().withArguments("clean", "-f:" + CleanTests.FRAMEWORK);
      }
      {
        final Clean command = new Clean();
        command.setFramework("  ");
        steps.add(command);
        clc.expectCommand().withArguments("clean");
      }
    });
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Clean command = new Clean();
        command.setRuntime(CleanTests.RUNTIME);
        steps.add(command);
        clc.expectCommand().withArguments("clean", "-r:" + CleanTests.RUNTIME);
      }
      {
        final Clean command = new Clean();
        command.setRuntime(null);
        steps.add(command);
        clc.expectCommand().withArguments("clean");
      }
    });
  }

}
