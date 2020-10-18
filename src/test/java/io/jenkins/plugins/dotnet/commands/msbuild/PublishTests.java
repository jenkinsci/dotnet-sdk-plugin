package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

import java.util.stream.Stream;

public final class PublishTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(Publish::new, check -> check.expectCommand().withArgument("publish"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setProject(PublishTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("publish", PublishTests.PROJECT));
  }

  @Test
  public void forceFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setForce(true);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--force"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setForce(false);
      return command;
    }, check -> check.expectCommand().withArgument("publish"));
  }

  private static final String FRAMEWORK = "net5.0";

  @Test
  public void frameworkOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setFramework(PublishTests.FRAMEWORK);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "-f:" + PublishTests.FRAMEWORK));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setFramework("  ");
      return command;
    }, check -> check.expectCommand().withArguments("publish"));
  }

  private static final String[] MANIFESTS = { "/path/to/first/manifest", "E:\\Second\\Manifest" };

  @Test
  public void manifestsOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setManifestsString(String.join(" ", PublishTests.MANIFESTS));
      return command;
    }, check -> {
      final String[] manifests = Stream.of(PublishTests.MANIFESTS).flatMap(s -> Stream.of("--manifest", s)).toArray(String[]::new);
      check.expectCommand().withArgument("publish").withArguments(manifests);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setManifestsString(PublishTests.MANIFESTS[0]);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--manifest", PublishTests.MANIFESTS[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setManifestsString(null);
      return command;
    }, check -> check.expectCommand().withArgument("publish"));
  }

  @Test
  public void noBuildFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setNoBuild(true);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--no-build"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setNoBuild(false);
      return command;
    }, check -> check.expectCommand().withArgument("publish"));
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setNoDependencies(true);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--no-dependencies"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setNoDependencies(false);
      return command;
    }, check -> check.expectCommand().withArgument("publish"));
  }

  @Test
  public void noRestoreFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setNoRestore(true);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--no-restore"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setNoRestore(false);
      return command;
    }, check -> check.expectCommand().withArgument("publish"));
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setRuntime(PublishTests.RUNTIME);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "-r:" + PublishTests.RUNTIME));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setRuntime(null);
      return command;
    }, check -> check.expectCommand().withArguments("publish"));
  }

  @Test
  public void selfContainedFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setSelfContained(true);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--self-contained", "true"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setSelfContained(false);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--self-contained", "false"));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setSelfContained(null);
      return command;
    }, check -> check.expectCommand().withArgument("publish"));
  }

  private static final String VERSION_SUFFIX = "unit.test";

  @Test
  public void versionSuffixOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setVersionSuffix(PublishTests.VERSION_SUFFIX);
      return command;
    }, check -> check.expectCommand().withArguments("publish", "--version-suffix", PublishTests.VERSION_SUFFIX));
    super.runCommandAndValidateProcessExecution(() -> {
      final Publish command = new Publish();
      command.setVersionSuffix(null);
      return command;
    }, check -> check.expectCommand().withArguments("publish"));
  }

}
