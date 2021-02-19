package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

import java.util.stream.Stream;

public final class BuildTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Build());
      clc.expectCommand().withArguments("build");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Build command = new Build();
      command.setProject(BuildTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("build", BuildTests.PROJECT);
    });
  }

  @Test
  public void forceFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setForce(true);
        steps.add(command);
        clc.expectCommand().withArguments("build", "--force");
      }
      {
        final Build command = new Build();
        command.setForce(false);
        steps.add(command);
        clc.expectCommand().withArgument("build");
      }
    });
  }

  private static final String FRAMEWORK = "net5.0";

  @Test
  public void frameworkOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setFramework(BuildTests.FRAMEWORK);
        steps.add(command);
        clc.expectCommand().withArguments("build", "-f:" + BuildTests.FRAMEWORK);
      }
      {
        final Build command = new Build();
        command.setFramework("  ");
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
    });
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setNoDependencies(true);
        steps.add(command);
        clc.expectCommand().withArguments("build", "--no-dependencies");
      }
      {
        final Build command = new Build();
        command.setNoDependencies(false);
        steps.add(command);
        clc.expectCommand().withArgument("build");
      }
    });
  }

  @Test
  public void noIncrementalFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setNoIncremental(true);
        steps.add(command);
        clc.expectCommand().withArguments("build", "--no-incremental");
      }
      {
        final Build command = new Build();
        command.setNoIncremental(false);
        steps.add(command);
        clc.expectCommand().withArgument("build");
      }
    });
  }

  @Test
  public void noRestoreFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setNoRestore(true);
        steps.add(command);
        clc.expectCommand().withArguments("build", "--no-restore");
      }
      {
        final Build command = new Build();
        command.setNoRestore(false);
        steps.add(command);
        clc.expectCommand().withArgument("build");
      }
    });
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setRuntime(BuildTests.RUNTIME);
        steps.add(command);
        clc.expectCommand().withArguments("build", "-r:" + BuildTests.RUNTIME);
      }
      {
        final Build command = new Build();
        command.setRuntime(null);
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
    });
  }

  private static final String[] TARGETS = {
    "Target1",
    "Target2"
  };

  @Test
  public void targetOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setTarget(BuildTests.TARGETS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("build", "-t:" + BuildTests.TARGETS[0]);
      }
      {
        final Build command = new Build();
        command.setTarget("  ; ");
        steps.add(command);
        clc.expectCommand().withArguments("build", "-t:;");
      }
      {
        final Build command = new Build();
        command.setTarget("  ");
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
      {
        final Build command = new Build();
        command.setTarget(null);
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
    });
  }

  @Test
  public void targetsOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setTargets(BuildTests.TARGETS);
        steps.add(command);
        {
          final Stream<String> targets = Stream.of(BuildTests.TARGETS).map(s -> "-t:" + s);
          clc.expectCommand().withArgument("build").withArguments(targets);
        }
      }
      {
        final Build command = new Build();
        command.setTargets(BuildTests.TARGETS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("build", "-t:" + BuildTests.TARGETS[0]);
      }
      {
        final Build command = new Build();
        command.setTargets("", null, "  ");
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
      {
        final Build command = new Build();
        command.setTargets((String[]) null);
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
    });
  }

  @Test
  public void targetsStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setTargetsString(String.join(" ", BuildTests.TARGETS));
        steps.add(command);
        {
          final Stream<String> targets = Stream.of(BuildTests.TARGETS).map(s -> "-t:" + s);
          clc.expectCommand().withArgument("build").withArguments(targets);
        }
      }
      {
        final Build command = new Build();
        command.setTargetsString(String.join(";", BuildTests.TARGETS));
        steps.add(command);
        {
          final Stream<String> targets = Stream.of(BuildTests.TARGETS).map(s -> "-t:" + s);
          clc.expectCommand().withArgument("build").withArguments(targets);
        }
      }
      {
        final Build command = new Build();
        command.setTargetsString(BuildTests.TARGETS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("build", "-t:" + BuildTests.TARGETS[0]);
      }
      {
        final Build command = new Build();
        command.setTargetsString(null);
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
    });
  }

  private static final String VERSION_SUFFIX = "unit.test";

  @Test
  public void versionSuffixOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Build command = new Build();
        command.setVersionSuffix(BuildTests.VERSION_SUFFIX);
        steps.add(command);
        clc.expectCommand().withArguments("build", "--version-suffix", BuildTests.VERSION_SUFFIX);
      }
      {
        final Build command = new Build();
        command.setVersionSuffix(null);
        steps.add(command);
        clc.expectCommand().withArguments("build");
      }
    });
  }

}
