package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;

public final class TestTests extends CommandTests {

  @org.junit.Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Test::new, check -> check.expectCommand().withArgument("test"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @org.junit.Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setProject(TestTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("test", TestTests.PROJECT));
  }

  private static final String FRAMEWORK = "net5.0";

  @org.junit.Test
  public void frameworkOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setFramework(TestTests.FRAMEWORK);
      return command;
    }, check -> check.expectCommand().withArguments("test", "-f:" + TestTests.FRAMEWORK));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setFramework("  ");
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String RUNTIME = "debian-x64";

  @org.junit.Test
  public void runtimeOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRuntime(TestTests.RUNTIME);
      return command;
    }, check -> check.expectCommand().withArguments("test", "-r:" + TestTests.RUNTIME));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRuntime(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  @org.junit.Test
  public void blameFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setBlame(true);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--blame"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setBlame(false);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String COLLECT = "stamps";

  @org.junit.Test
  public void collectOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setCollect(TestTests.COLLECT);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--collect", TestTests.COLLECT));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setCollect(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String DIAG = "gnostic";

  @org.junit.Test
  public void diagOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setDiag(TestTests.DIAG);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--diag", TestTests.DIAG));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setDiag(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String FILTER = "coffee";

  @org.junit.Test
  public void filterOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setFilter(TestTests.FILTER);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--filter", TestTests.FILTER));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setFilter(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  @org.junit.Test
  public void listTestsFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setListTests(true);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--list-tests"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setListTests(false);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String LOGGER = "lumberjack";

  @org.junit.Test
  public void loggerOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setLogger(TestTests.LOGGER);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--logger", TestTests.LOGGER));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setLogger(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  @org.junit.Test
  public void noBuildFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setNoBuild(true);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--no-build"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setNoBuild(false);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  @org.junit.Test
  public void noRestoreFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setNoRestore(true);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--no-restore"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setNoRestore(false);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String RESULTS_DIRECTORY = "/path/to/results";

  @org.junit.Test
  public void resultsDirectoryOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setResultsDirectory(TestTests.RESULTS_DIRECTORY);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--results-directory", TestTests.RESULTS_DIRECTORY));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setResultsDirectory(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String RUNSETTINGS = "# A comment\n" +
    "\n" +
    "# That was a blank line\n" +
    "MyRunSetting=Value \\\n" +
    " Containing=An Equals Sign\n" +
    "# A Comment";

  @org.junit.Test
  public void runSettingsOptionWorks() throws Exception {
    // Note: this cannot test a string with multiple settings, because their order in the result cannot be guaranteed.
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRunSettingsString(TestTests.RUNSETTINGS);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--", "MyRunSetting=Value Containing=An Equals Sign"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRunSettingsString("SingleIdentifier");
      return command;
    }, check -> check.expectCommand().withArguments("test", "--", "SingleIdentifier="));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRunSettingsString("  ");
      return command;
    }, check -> check.expectCommand().withArguments("test", "--"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRunSettingsString("");
      return command;
    }, check -> check.expectCommand().withArgument("test"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setRunSettingsString(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String SETTINGS = "/path/to/my.runsettings";

  @org.junit.Test
  public void settingsOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setSettings(TestTests.SETTINGS);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--settings", TestTests.SETTINGS));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setSettings(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

  private static final String TEST_ADAPTER_PATH = "/path/to/test/adapter";

  @org.junit.Test
  public void testAdapterPathOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setTestAdapterPath(TestTests.TEST_ADAPTER_PATH);
      return command;
    }, check -> check.expectCommand().withArguments("test", "--test-adapter-path", TestTests.TEST_ADAPTER_PATH));
    super.runCommandAndValidateProcessExecution(() -> {
      final Test command = new Test();
      command.setTestAdapterPath(null);
      return command;
    }, check -> check.expectCommand().withArgument("test"));
  }

}
