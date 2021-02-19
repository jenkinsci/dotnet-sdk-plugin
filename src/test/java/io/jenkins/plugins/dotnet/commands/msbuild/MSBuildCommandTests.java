package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

public final class MSBuildCommandTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new MSBuildCommand());
      clc.expectCommand().withArguments();
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final MSBuildCommand command = new MSBuildCommand("unit-test");
      command.setProject(MSBuildCommandTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("unit-test", MSBuildCommandTests.PROJECT);
    });
  }

  private static final String CONFIGURATION = "Release";

  @Test
  public void configurationOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setConfiguration(MSBuildCommandTests.CONFIGURATION);
        steps.add(command);
        clc.expectCommand().withArgument("-c:" + MSBuildCommandTests.CONFIGURATION);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setConfiguration(null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  @Test
  public void nologoFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setNologo(true);
        steps.add(command);
        clc.expectCommand().withArguments("--nologo");
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setNologo(false);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  private static final String[] OPTIONS = {
    "--random",
    "-o:ptions",
    "Including Whatever"
  };

  private static final String OPTIONS_STRING = "--random -o:ptions 'Including Whatever'";

  @Test
  public void optionOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOption(MSBuildCommandTests.OPTIONS[0]);
        steps.add(command);
        clc.expectCommand().withArgument(MSBuildCommandTests.OPTIONS[0]);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOption(MSBuildCommandTests.OPTIONS[1]);
        steps.add(command);
        clc.expectCommand().withArgument(MSBuildCommandTests.OPTIONS[1]);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOption(MSBuildCommandTests.OPTIONS[2]);
        steps.add(command);
        clc.expectCommand().withArgument(MSBuildCommandTests.OPTIONS[2]);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOption(" ");
        steps.add(command);
        clc.expectCommand();
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOption(null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  @Test
  public void optionsOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOptions(MSBuildCommandTests.OPTIONS);
        steps.add(command);
        clc.expectCommand().withArguments(MSBuildCommandTests.OPTIONS);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOptions(MSBuildCommandTests.OPTIONS_STRING);
        steps.add(command);
        clc.expectCommand().withArgument(MSBuildCommandTests.OPTIONS_STRING);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOptions(null, "", "  ");
        steps.add(command);
        clc.expectCommand();
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOptions((String[]) null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  @Test
  public void optionsStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOptionsString(MSBuildCommandTests.OPTIONS_STRING);
        steps.add(command);
        clc.expectCommand().withArguments(MSBuildCommandTests.OPTIONS);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOptionsString(null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  private static final String OUTPUT_DIRECTORY = "/path/to/output";

  @Test
  public void outputDirectoryOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOutputDirectory(MSBuildCommandTests.OUTPUT_DIRECTORY);
        steps.add(command);
        clc.expectCommand().withArguments("--output", MSBuildCommandTests.OUTPUT_DIRECTORY);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setOutputDirectory(null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  private static final String PROPERTIES_STRING = "# A comment\n" +
    "\n" +
    "# That was a blank line\n" +
    "MyProperty=Value \\\n" +
    " Containing=An Equals Sign\n" +
    "# A Comment";

  @Test
  public void propertiesStringOptionWorks() throws Exception {
    // Note: this cannot test a string with multiple properties, because their order in the result cannot be guaranteed.
    super.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setPropertiesString(MSBuildCommandTests.PROPERTIES_STRING);
        steps.add(command);
        clc.expectCommand().withArgument("-p:MyProperty=Value Containing=An Equals Sign");
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setPropertiesString("SingleIdentifier");
        steps.add(command);
        clc.expectCommand().withArgument("-p:SingleIdentifier=");
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setPropertiesString("  ");
        steps.add(command);
        clc.expectCommand();
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setPropertiesString("");
        steps.add(command);
        clc.expectCommand();
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setPropertiesString(null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  @Test
  public void shutDownBuildServersFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setShutDownBuildServers(true);
        steps.add(command);
        clc.expectCommand();
        clc.expectCommand().withArguments("build-server", "shutdown");
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setShutDownBuildServers(false);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

  private static final String VERBOSITY = "minimal";

  @Test
  public void verbosityOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setVerbosity(MSBuildCommandTests.VERBOSITY);
        steps.add(command);
        clc.expectCommand().withArgument("-v:" + MSBuildCommandTests.VERBOSITY);
      }
      {
        final MSBuildCommand command = new MSBuildCommand();
        command.setVerbosity(null);
        steps.add(command);
        clc.expectCommand();
      }
    });
  }

}
