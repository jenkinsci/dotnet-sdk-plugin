package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

import java.util.stream.Stream;

public final class RestoreTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Restore());
      clc.expectCommand().withArguments("restore");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Restore command = new Restore();
      command.setProject(RestoreTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("restore", RestoreTests.PROJECT);
    });
  }

  private static final String CONFIGFILE = "/path/to/OldGet.config";

  @Test
  public void configfileOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setConfigfile(RestoreTests.CONFIGFILE);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--configfile", RestoreTests.CONFIGFILE);
      }
      {
        final Restore command = new Restore();
        command.setConfigfile(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void disableParallelFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setDisableParallel(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--disable-parallel");
      }
      {
        final Restore command = new Restore();
        command.setDisableParallel(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void forceFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setForce(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--force");
      }
      {
        final Restore command = new Restore();
        command.setForce(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void forceEvaluateFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setForceEvaluate(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--force-evaluate");
      }
      {
        final Restore command = new Restore();
        command.setForceEvaluate(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void ignoreFailedSourcesFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setIgnoreFailedSources(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--ignore-failed-sources");
      }
      {
        final Restore command = new Restore();
        command.setIgnoreFailedSources(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  private static final String LOCK_FILE_PATH = "/path/to/lock/file";

  @Test
  public void lockFilePathOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setLockFilePath(RestoreTests.LOCK_FILE_PATH);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--lock-file-path", RestoreTests.LOCK_FILE_PATH);
      }
      {
        final Restore command = new Restore();
        command.setLockFilePath(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void lockedModeFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setLockedMode(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--locked-mode");
      }
      {
        final Restore command = new Restore();
        command.setLockedMode(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void noCacheFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setNoCache(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--no-cache");
      }
      {
        final Restore command = new Restore();
        command.setNoCache(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setNoDependencies(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--no-dependencies");
      }
      {
        final Restore command = new Restore();
        command.setNoDependencies(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  private static final String PACKAGES = "/path/to/packages";

  @Test
  public void packagesOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setPackages(RestoreTests.PACKAGES);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--packages", RestoreTests.PACKAGES);
      }
      {
        final Restore command = new Restore();
        command.setPackages(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  private static final String[] RUNTIMES = {
    "win10",
    "ios"
  };

  @Test
  public void runtimeOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setRuntime(RestoreTests.RUNTIMES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-r:" + RestoreTests.RUNTIMES[0]);
      }
      {
        final Restore command = new Restore();
        command.setRuntime(" ");
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
      {
        final Restore command = new Restore();
        command.setRuntime(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void runtimesOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setRuntimes(RestoreTests.RUNTIMES);
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(RestoreTests.RUNTIMES).map(s -> "-r:" + s);
          clc.expectCommand().withArgument("restore").withArguments(sources);
        }
      }
      {
        final Restore command = new Restore();
        command.setRuntimes(RestoreTests.RUNTIMES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-r:" + RestoreTests.RUNTIMES[0]);
      }
      {
        final Restore command = new Restore();
        command.setRuntimes("  ", null, "");
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
      {
        final Restore command = new Restore();
        command.setRuntimes((String[]) null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void runtimesStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setRuntimesString(String.join(" ", RestoreTests.RUNTIMES));
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(RestoreTests.RUNTIMES).map(s -> "-r:" + s);
          clc.expectCommand().withArgument("restore").withArguments(sources);
        }
      }
      {
        final Restore command = new Restore();
        command.setRuntimesString(RestoreTests.RUNTIMES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-r:" + RestoreTests.RUNTIMES[0]);
      }
      {
        final Restore command = new Restore();
        command.setRuntimesString(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  private static final String[] SOURCES = {
    "source1",
    "source2"
  };

  @Test
  public void sourceOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setSource(RestoreTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-s:" + RestoreTests.SOURCES[0]);
      }
      {
        final Restore command = new Restore();
        command.setSource("");
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
      {
        final Restore command = new Restore();
        command.setSource(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void sourcesOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setSources(RestoreTests.SOURCES);
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(RestoreTests.SOURCES).map(s -> "-s:" + s);
          clc.expectCommand().withArgument("restore").withArguments(sources);
        }
      }
      {
        final Restore command = new Restore();
        command.setSources(RestoreTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-s:" + RestoreTests.SOURCES[0]);
      }
      {
        final Restore command = new Restore();
        command.setSources(null, null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
      {
        final Restore command = new Restore();
        command.setSources((String[]) null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void sourcesStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setSourcesString(String.join(" ", RestoreTests.SOURCES));
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(RestoreTests.SOURCES).map(s -> "-s:" + s);
          clc.expectCommand().withArgument("restore").withArguments(sources);
        }
      }
      {
        final Restore command = new Restore();
        command.setSourcesString(RestoreTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-s:" + RestoreTests.SOURCES[0]);
      }
      {
        final Restore command = new Restore();
        command.setSourcesString(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void useLockFileFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setUseLockFile(true);
        steps.add(command);
        clc.expectCommand().withArguments("restore", "--use-lock-file");
      }
      {
        final Restore command = new Restore();
        command.setUseLockFile(false);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

  @Test
  public void verbosityOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setVerbosity("diag");
        steps.add(command);
        clc.expectCommand().withArguments("restore", "-v:diag");
      }
      {
        final Restore command = new Restore();
        command.setVerbosity(null);
        steps.add(command);
        clc.expectCommand().withArguments("restore");
      }
    });
  }

}
