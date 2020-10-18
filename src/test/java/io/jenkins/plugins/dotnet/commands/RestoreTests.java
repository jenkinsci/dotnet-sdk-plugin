package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

import java.util.stream.Stream;

public final class RestoreTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Restore::new, check -> check.expectCommand().withArgument("restore"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setProject(RestoreTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("restore", RestoreTests.PROJECT));
  }

  private static final String CONFIGFILE = "/path/to/OldGet.config";

  @Test
  public void configfileOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setConfigfile(RestoreTests.CONFIGFILE);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--configfile", RestoreTests.CONFIGFILE));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setConfigfile(null);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void disableParallelFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setDisableParallel(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--disable-parallel"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setDisableParallel(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void forceFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setForce(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--force"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setForce(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void forceEvaluateFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setForceEvaluate(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--force-evaluate"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setForceEvaluate(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void ignoreFailedSourcesFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setIgnoreFailedSources(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--ignore-failed-sources"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setIgnoreFailedSources(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  private static final String LOCK_FILE_PATH = "/path/to/lock/file";

  @Test
  public void lockFilePathOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setLockFilePath(RestoreTests.LOCK_FILE_PATH);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--lock-file-path", RestoreTests.LOCK_FILE_PATH));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setLockFilePath(null);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void lockedModeFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setLockedMode(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--locked-mode"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setLockedMode(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void noCacheFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setNoCache(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--no-cache"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setNoCache(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setNoDependencies(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--no-dependencies"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setNoDependencies(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  private static final String PACKAGES = "/path/to/packages";

  @Test
  public void packagesOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setPackages(RestoreTests.PACKAGES);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--packages", RestoreTests.PACKAGES));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setPackages(null);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  private static final String[] RUNTIMES = { "win10", "ios" };

  @Test
  public void runtimesOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setRuntimesString(String.join(" ", RestoreTests.RUNTIMES));
      return command;
    }, check -> {
      final String[] sources = Stream.of(RestoreTests.RUNTIMES).map(s -> "-r:" + s).toArray(String[]::new);
      check.expectCommand().withArgument("restore").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setRuntimesString(RestoreTests.RUNTIMES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "-r:" + RestoreTests.RUNTIMES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setRuntimesString(null);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  private static final String[] SOURCES = { "source1", "source2" };

  @Test
  public void sourcesOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setSources(String.join(" ", RestoreTests.SOURCES));
      return command;
    }, check -> {
      final String[] sources = Stream.of(RestoreTests.SOURCES).map(s -> "-s:" + s).toArray(String[]::new);
      check.expectCommand().withArgument("restore").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setSources(RestoreTests.SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "-s:" + RestoreTests.SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setSources(null);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void useLockFileFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setUseLockFile(true);
      return command;
    }, check -> check.expectCommand().withArguments("restore", "--use-lock-file"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setUseLockFile(false);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

  @Test
  public void verbosityOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setVerbosity("diag");
      return command;
    }, check -> check.expectCommand().withArguments("restore", "-v:diag"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setVerbosity(null);
      return command;
    }, check -> check.expectCommand().withArguments("restore"));
  }

}
