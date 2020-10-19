package io.jenkins.plugins.dotnet.commands.tool;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

import java.util.stream.Stream;

public final class RestoreTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Restore::new, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  private static final String[] ADDITIONAL_SOURCES = { "MyGet", "OldGet", "YourGet" };

  @Test
  public void additionalSourceOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSource(RestoreTests.ADDITIONAL_SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--add-source", RestoreTests.ADDITIONAL_SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSource("  ");
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSource(null);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  @Test
  public void additionalSourcesOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSources(RestoreTests.ADDITIONAL_SOURCES);
      return command;
    }, check -> {
      final String[] sources = Stream.of(RestoreTests.ADDITIONAL_SOURCES).flatMap(s -> Stream.of("--add-source", s)).toArray(String[]::new);
      check.expectCommand().withArguments("tool", "restore").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSources(RestoreTests.ADDITIONAL_SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--add-source", RestoreTests.ADDITIONAL_SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSources(null, "", "  ");
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSources((String[]) null);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  @Test
  public void additionalSourcesStringOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSourcesString(String.join(" ", RestoreTests.ADDITIONAL_SOURCES));
      return command;
    }, check -> {
      final String[] sources = Stream.of(RestoreTests.ADDITIONAL_SOURCES).flatMap(s -> Stream.of("--add-source", s)).toArray(String[]::new);
      check.expectCommand().withArguments("tool", "restore").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSourcesString(RestoreTests.ADDITIONAL_SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--add-source", RestoreTests.ADDITIONAL_SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setAdditionalSourcesString(null);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  private static final String CONFIGFILE = "/path/to/OldGet.config";

  @Test
  public void configfileOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setConfigfile(RestoreTests.CONFIGFILE);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--configfile", RestoreTests.CONFIGFILE));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setConfigfile(null);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  @Test
  public void disableParallelFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setDisableParallel(true);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--disable-parallel"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setDisableParallel(false);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  @Test
  public void ignoreFailedSourcesFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setIgnoreFailedSources(true);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--ignore-failed-sources"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setIgnoreFailedSources(false);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  @Test
  public void noCacheFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setNoCache(true);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--no-cache"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setNoCache(false);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  private static final String TOOL_MANIFEST = "/path/to/tool.manifest";

  @Test
  public void toolManifestOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setToolManifest(RestoreTests.TOOL_MANIFEST);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "--tool-manifest", RestoreTests.TOOL_MANIFEST));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setToolManifest(null);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

  @Test
  public void verbosityOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setVerbosity("diag");
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore", "-v:diag"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Restore command = new Restore();
      command.setVerbosity(null);
      return command;
    }, check -> check.expectCommand().withArguments("tool", "restore"));
  }

}
