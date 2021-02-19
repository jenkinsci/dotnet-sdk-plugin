package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;

public final class TestTests extends CommandTests {

  @org.junit.Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Test());
      clc.expectCommand().withArguments("test");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @org.junit.Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Test command = new Test();
      command.setProject(TestTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("test", TestTests.PROJECT);
    });
  }

  private static final String FRAMEWORK = "net5.0";

  @org.junit.Test
  public void frameworkOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setFramework(TestTests.FRAMEWORK);
        steps.add(command);
        clc.expectCommand().withArguments("test", "-f:" + TestTests.FRAMEWORK);
      }
      {
        final Test command = new Test();
        command.setFramework("  ");
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String RUNTIME = "debian-x64";

  @org.junit.Test
  public void runtimeOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setRuntime(TestTests.RUNTIME);
        steps.add(command);
        clc.expectCommand().withArguments("test", "-r:" + TestTests.RUNTIME);
      }
      {
        final Test command = new Test();
        command.setRuntime(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  @org.junit.Test
  public void blameFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setBlame(true);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--blame");
      }
      {
        final Test command = new Test();
        command.setBlame(false);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String COLLECT = "stamps";

  @org.junit.Test
  public void collectOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setCollect(TestTests.COLLECT);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--collect", TestTests.COLLECT);
      }
      {
        final Test command = new Test();
        command.setCollect(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String DIAG = "gnostic";

  @org.junit.Test
  public void diagOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setDiag(TestTests.DIAG);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--diag", TestTests.DIAG);
      }
      {
        final Test command = new Test();
        command.setDiag(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String FILTER = "coffee";

  @org.junit.Test
  public void filterOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setFilter(TestTests.FILTER);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--filter", TestTests.FILTER);
      }
      {
        final Test command = new Test();
        command.setFilter(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  @org.junit.Test
  public void listTestsFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setListTests(true);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--list-tests");
      }
      {
        final Test command = new Test();
        command.setListTests(false);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String LOGGER = "lumberjack";

  @org.junit.Test
  public void loggerOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setLogger(TestTests.LOGGER);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--logger", TestTests.LOGGER);
      }
      {
        final Test command = new Test();
        command.setLogger(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  @org.junit.Test
  public void noBuildFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setNoBuild(true);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--no-build");
      }
      {
        final Test command = new Test();
        command.setNoBuild(false);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  @org.junit.Test
  public void noRestoreFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setNoRestore(true);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--no-restore");
      }
      {
        final Test command = new Test();
        command.setNoRestore(false);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String RESULTS_DIRECTORY = "/path/to/results";

  @org.junit.Test
  public void resultsDirectoryOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setResultsDirectory(TestTests.RESULTS_DIRECTORY);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--results-directory", TestTests.RESULTS_DIRECTORY);
      }
      {
        final Test command = new Test();
        command.setResultsDirectory(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String RUNSETTINGS_STRING = "# A comment\n" +
    "\n" +
    "# That was a blank line\n" +
    "MyRunSetting=Value \\\n" +
    " Containing=An Equals Sign\n" +
    "# A Comment";

  @org.junit.Test
  public void runSettingsStringOptionWorks() throws Exception {
    // Note: this cannot test a string with multiple settings, because their order in the result cannot be guaranteed.
    super.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setRunSettingsString(TestTests.RUNSETTINGS_STRING);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--", "MyRunSetting=Value Containing=An Equals Sign");
      }
      {
        final Test command = new Test();
        command.setRunSettingsString("SingleIdentifier");
        steps.add(command);
        clc.expectCommand().withArguments("test", "--", "SingleIdentifier=");
      }
      {
        final Test command = new Test();
        command.setRunSettingsString("  ");
        steps.add(command);
        clc.expectCommand().withArguments("test", "--");
      }
      {
        final Test command = new Test();
        command.setRunSettingsString("");
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
      {
        final Test command = new Test();
        command.setRunSettingsString(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String SETTINGS = "/path/to/my.runsettings";

  @org.junit.Test
  public void settingsOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setSettings(TestTests.SETTINGS);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--settings", TestTests.SETTINGS);
      }
      {
        final Test command = new Test();
        command.setSettings(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

  private static final String TEST_ADAPTER_PATH = "/path/to/test/adapter";

  @org.junit.Test
  public void testAdapterPathOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Test command = new Test();
        command.setTestAdapterPath(TestTests.TEST_ADAPTER_PATH);
        steps.add(command);
        clc.expectCommand().withArguments("test", "--test-adapter-path", TestTests.TEST_ADAPTER_PATH);
      }
      {
        final Test command = new Test();
        command.setTestAdapterPath(null);
        steps.add(command);
        clc.expectCommand().withArgument("test");
      }
    });
  }

}
