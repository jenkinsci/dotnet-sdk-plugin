package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class PackTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Pack::new, check -> check.expectCommand().withArgument("pack"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setProject(PackTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("pack", PackTests.PROJECT));
  }

  @Test
  public void forceFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setForce(true);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--force"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setForce(false);
      return command;
    }, check -> check.expectCommand().withArgument("pack"));
  }

  @Test
  public void includeSourceFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setIncludeSource(true);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--include-source"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setIncludeSource(false);
      return command;
    }, check -> check.expectCommand().withArgument("pack"));
  }

  @Test
  public void includeSymbolsFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setIncludeSymbols(true);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--include-symbols"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setIncludeSymbols(false);
      return command;
    }, check -> check.expectCommand().withArgument("pack"));
  }

  @Test
  public void noBuildFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setNoBuild(true);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--no-build"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setNoBuild(false);
      return command;
    }, check -> check.expectCommand().withArgument("pack"));
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setNoDependencies(true);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--no-dependencies"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setNoDependencies(false);
      return command;
    }, check -> check.expectCommand().withArgument("pack"));
  }

  @Test
  public void noRestoreFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setNoRestore(true);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--no-restore"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setNoRestore(false);
      return command;
    }, check -> check.expectCommand().withArgument("pack"));
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setRuntime(PackTests.RUNTIME);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "-r:" + PackTests.RUNTIME));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setRuntime(null);
      return command;
    }, check -> check.expectCommand().withArguments("pack"));
  }

  private static final String VERSION_SUFFIX = "unit.test";

  @Test
  public void versionSuffixOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setVersionSuffix(PackTests.VERSION_SUFFIX);
      return command;
    }, check -> check.expectCommand().withArguments("pack", "--version-suffix", PackTests.VERSION_SUFFIX));
    super.runCommandAndValidateProcessExecution(() -> {
      final Pack command = new Pack();
      command.setVersionSuffix(null);
      return command;
    }, check -> check.expectCommand().withArguments("pack"));
  }

}
