package io.jenkins.plugins.dotnet.commands.msbuild;

import hudson.Util;
import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class MSBuildCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(MSBuildCommand::new, CommandLineChecker::expectCommand);
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand("unit-test");
      command.setProject(MSBuildCommandTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("unit-test", MSBuildCommandTests.PROJECT));
  }

  private static final String CONFIGURATION = "Release";

  @Test
  public void configurationOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setConfiguration(MSBuildCommandTests.CONFIGURATION);
      return command;
    }, check -> check.expectCommand().withArgument("-c:" + MSBuildCommandTests.CONFIGURATION));
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setConfiguration(null);
      return command;
    }, CommandLineChecker::expectCommand);
  }

  @Test
  public void nologoFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setNologo(true);
      return command;
    }, commandLineChecker -> commandLineChecker.expectCommand().withArguments("--nologo"));
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setNologo(false);
      return command;
    }, CommandLineChecker::expectCommand);
  }

  private static final String OPTIONS = "--random -o:ptions 'Including Whatever'";

  @Test
  public void optionsOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setOptions(MSBuildCommandTests.OPTIONS);
      return command;
    }, check -> check.expectCommand().withArguments(Util.tokenize(MSBuildCommandTests.OPTIONS)));
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setOptions(null);
      return command;
    }, CommandLineChecker::expectCommand);
  }

  private static final String OUTPUT_DIRECTORY = "/path/to/output";

  @Test
  public void outputDirectoryOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setOutputDirectory(MSBuildCommandTests.OUTPUT_DIRECTORY);
      return command;
    }, check -> check.expectCommand().withArguments("--output", MSBuildCommandTests.OUTPUT_DIRECTORY));
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setOutputDirectory(null);
      return command;
    }, CommandLineChecker::expectCommand);
  }

  private static final String PROPERTIES = "# A comment\n" +
    "\n" +
    "# That was a blank line\n" +
    "MyProperty=Value \\\n" +
    " Containing=An Equals Sign\n" +
    "# A Comment";

  @Test
  public void propertiesOptionWorks() throws Exception {
    // Note: this cannot test a string with multiple properties, because their order in the result cannot be guaranteed.
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setProperties(MSBuildCommandTests.PROPERTIES);
      return command;
    }, check -> check.expectCommand().withArgument("-p:MyProperty=Value Containing=An Equals Sign"));
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setProperties(null);
      return command;
    }, CommandLineChecker::expectCommand);
  }

  @Test
  public void shutDownBuildServersFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setShutDownBuildServers(true);
      return command;
    }, commandLineChecker -> {
      commandLineChecker.expectCommand();
      commandLineChecker.expectCommand().withArguments("build-server", "shutdown");
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setShutDownBuildServers(false);
      return command;
    }, CommandLineChecker::expectCommand);
  }

  private static final String VERBOSITY = "minimal";

  @Test
  public void verbosityOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setVerbosity(MSBuildCommandTests.VERBOSITY);
      return command;
    }, check -> check.expectCommand().withArgument("-v:" + MSBuildCommandTests.VERBOSITY));
    super.runCommandAndValidateProcessExecution(() -> {
      final MSBuildCommand command = new MSBuildCommand();
      command.setVerbosity(null);
      return command;
    }, CommandLineChecker::expectCommand);
  }

}
