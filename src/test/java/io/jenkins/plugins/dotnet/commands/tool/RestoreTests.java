package io.jenkins.plugins.dotnet.commands.tool;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

import java.util.stream.Stream;

public final class RestoreTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Restore());
      clc.expectCommand().withArguments("tool", "restore");
    });
  }

  private static final String[] ADDITIONAL_SOURCES = {
    "MyGet",
    "OldGet",
    "YourGet"
  };

  @Test
  public void additionalSourceOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setAdditionalSource(RestoreTests.ADDITIONAL_SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore", "--add-source", RestoreTests.ADDITIONAL_SOURCES[0]);
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSource("  ");
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSource(null);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
    });
  }

  @Test
  public void additionalSourcesOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setAdditionalSources(RestoreTests.ADDITIONAL_SOURCES);
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(RestoreTests.ADDITIONAL_SOURCES).flatMap(s -> Stream.of("--add-source", s));
          clc.expectCommand().withArguments("tool", "restore").withArguments(sources);
        }
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSources(RestoreTests.ADDITIONAL_SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore", "--add-source", RestoreTests.ADDITIONAL_SOURCES[0]);
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSources(null, "", "  ");
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSources((String[]) null);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
    });
  }

  @Test
  public void additionalSourcesStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setAdditionalSourcesString(String.join(" ", RestoreTests.ADDITIONAL_SOURCES));
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(RestoreTests.ADDITIONAL_SOURCES).flatMap(s -> Stream.of("--add-source", s));
          clc.expectCommand().withArguments("tool", "restore").withArguments(sources);
        }
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSourcesString(RestoreTests.ADDITIONAL_SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore", "--add-source", RestoreTests.ADDITIONAL_SOURCES[0]);
      }
      {
        final Restore command = new Restore();
        command.setAdditionalSourcesString(null);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
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
        clc.expectCommand().withArguments("tool", "restore", "--configfile", RestoreTests.CONFIGFILE);
      }
      {
        final Restore command = new Restore();
        command.setConfigfile(null);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
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
        clc.expectCommand().withArguments("tool", "restore", "--disable-parallel");
      }
      {
        final Restore command = new Restore();
        command.setDisableParallel(false);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
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
        clc.expectCommand().withArguments("tool", "restore", "--ignore-failed-sources");
      }
      {
        final Restore command = new Restore();
        command.setIgnoreFailedSources(false);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
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
        clc.expectCommand().withArguments("tool", "restore", "--no-cache");
      }
      {
        final Restore command = new Restore();
        command.setNoCache(false);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
    });
  }

  private static final String TOOL_MANIFEST = "/path/to/tool.manifest";

  @Test
  public void toolManifestOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Restore command = new Restore();
        command.setToolManifest(RestoreTests.TOOL_MANIFEST);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore", "--tool-manifest", RestoreTests.TOOL_MANIFEST);
      }
      {
        final Restore command = new Restore();
        command.setToolManifest(null);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
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
        clc.expectCommand().withArguments("tool", "restore", "-v:diag");
      }
      {
        final Restore command = new Restore();
        command.setVerbosity(null);
        steps.add(command);
        clc.expectCommand().withArguments("tool", "restore");
      }
    });
  }

}
