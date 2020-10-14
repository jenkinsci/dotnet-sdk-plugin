package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

import java.util.stream.Stream;

public final class BuildTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Build::new, check -> check.expectCommand().withArgument("build"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setProject(BuildTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("build", BuildTests.PROJECT));
  }

  @Test
  public void forceFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setForce(true);
      return command;
    }, check -> check.expectCommand().withArguments("build", "--force"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setForce(false);
      return command;
    }, check -> check.expectCommand().withArgument("build"));
  }

  private static final String FRAMEWORK = "net5.0";

  @Test
  public void frameworkOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setFramework(BuildTests.FRAMEWORK);
      return command;
    }, check -> check.expectCommand().withArguments("build", "-f:" + BuildTests.FRAMEWORK));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setFramework("  ");
      return command;
    }, check -> check.expectCommand().withArguments("build"));
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setNoDependencies(true);
      return command;
    }, check -> check.expectCommand().withArguments("build", "--no-dependencies"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setNoDependencies(false);
      return command;
    }, check -> check.expectCommand().withArgument("build"));
  }

  @Test
  public void noIncrementalFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setNoIncremental(true);
      return command;
    }, check -> check.expectCommand().withArguments("build", "--no-incremental"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setNoIncremental(false);
      return command;
    }, check -> check.expectCommand().withArgument("build"));
  }

  @Test
  public void noRestoreFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setNoRestore(true);
      return command;
    }, check -> check.expectCommand().withArguments("build", "--no-restore"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setNoRestore(false);
      return command;
    }, check -> check.expectCommand().withArgument("build"));
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setRuntime(BuildTests.RUNTIME);
      return command;
    }, check -> check.expectCommand().withArguments("build", "-r:" + BuildTests.RUNTIME));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setRuntime(null);
      return command;
    }, check -> check.expectCommand().withArguments("build"));
  }

  private static final String[] TARGETS = { "Target1", "Target2" };

  @Test
  public void targetStringOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargetsString(String.join(" ", BuildTests.TARGETS));
      return command;
    }, check -> {
      final String[] targets = Stream.of(BuildTests.TARGETS).map(s -> "-t:" + s).toArray(String[]::new);
      check.expectCommand().withArgument("build").withArguments(targets);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargetsString(String.join(";", BuildTests.TARGETS));
      return command;
    }, check -> {
      final String[] targets = Stream.of(BuildTests.TARGETS).map(s -> "-t:" + s).toArray(String[]::new);
      check.expectCommand().withArgument("build").withArguments(targets);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargetsString(BuildTests.TARGETS[0]);
      return command;
    }, check -> check.expectCommand().withArguments("build", "-t:" + BuildTests.TARGETS[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargetsString(null);
      return command;
    }, check -> check.expectCommand().withArguments("build"));
  }

  @Test
  public void targetsOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargets(BuildTests.TARGETS);
      return command;
    }, check -> {
      final String[] targets = Stream.of(BuildTests.TARGETS).map(s -> "-t:" + s).toArray(String[]::new);
      check.expectCommand().withArgument("build").withArguments(targets);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargets(BuildTests.TARGETS[0]);
      return command;
    }, check -> check.expectCommand().withArguments("build", "-t:" + BuildTests.TARGETS[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setTargets((String[]) null);
      return command;
    }, check -> check.expectCommand().withArguments("build"));
  }

  private static final String VERSION_SUFFIX = "unit.test";

  @Test
  public void versionSuffixOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setVersionSuffix(BuildTests.VERSION_SUFFIX);
      return command;
    }, check -> check.expectCommand().withArguments("build", "--version-suffix", BuildTests.VERSION_SUFFIX));
    super.runCommandAndValidateProcessExecution(() -> {
      final Build command = new Build();
      command.setVersionSuffix(null);
      return command;
    }, check -> check.expectCommand().withArguments("build"));
  }

}
