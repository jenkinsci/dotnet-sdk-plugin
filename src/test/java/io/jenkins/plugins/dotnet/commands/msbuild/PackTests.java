package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class PackTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Pack());
      clc.expectCommand().withArguments("pack");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Pack command = new Pack();
      command.setProject(PackTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("pack", PackTests.PROJECT);
    });
  }

  @Test
  public void forceFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setForce(true);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--force");
      }
      {
        final Pack command = new Pack();
        command.setForce(false);
        steps.add(command);
        clc.expectCommand().withArgument("pack");
      }
    });
  }

  @Test
  public void includeSourceFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setIncludeSource(true);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--include-source");
      }
      {
        final Pack command = new Pack();
        command.setIncludeSource(false);
        steps.add(command);
        clc.expectCommand().withArgument("pack");
      }
    });
  }

  @Test
  public void includeSymbolsFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setIncludeSymbols(true);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--include-symbols");
      }
      {
        final Pack command = new Pack();
        command.setIncludeSymbols(false);
        steps.add(command);
        clc.expectCommand().withArgument("pack");
      }
    });
  }

  @Test
  public void noBuildFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setNoBuild(true);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--no-build");
      }
      {
        final Pack command = new Pack();
        command.setNoBuild(false);
        steps.add(command);
        clc.expectCommand().withArgument("pack");
      }
    });
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setNoDependencies(true);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--no-dependencies");
      }
      {
        final Pack command = new Pack();
        command.setNoDependencies(false);
        steps.add(command);
        clc.expectCommand().withArgument("pack");
      }
    });
  }

  @Test
  public void noRestoreFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setNoRestore(true);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--no-restore");
      }
      {
        final Pack command = new Pack();
        command.setNoRestore(false);
        steps.add(command);
        clc.expectCommand().withArgument("pack");
      }
    });
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setRuntime(PackTests.RUNTIME);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "-r:" + PackTests.RUNTIME);
      }
      {
        final Pack command = new Pack();
        command.setRuntime(null);
        steps.add(command);
        clc.expectCommand().withArguments("pack");
      }
    });
  }

  private static final String VERSION_SUFFIX = "unit.test";

  @Test
  public void versionSuffixOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Pack command = new Pack();
        command.setVersionSuffix(PackTests.VERSION_SUFFIX);
        steps.add(command);
        clc.expectCommand().withArguments("pack", "--version-suffix", PackTests.VERSION_SUFFIX);
      }
      {
        final Pack command = new Pack();
        command.setVersionSuffix(null);
        steps.add(command);
        clc.expectCommand().withArguments("pack");
      }
    });
  }

}
