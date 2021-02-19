package io.jenkins.plugins.dotnet.commands.msbuild;

import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.junit.Test;

import java.util.stream.Stream;

public final class PublishTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Publish());
      clc.expectCommand().withArguments("publish");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Publish command = new Publish();
      command.setProject(PublishTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("publish", PublishTests.PROJECT);
    });
  }

  @Test
  public void forceFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setForce(true);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--force");
      }
      {
        final Publish command = new Publish();
        command.setForce(false);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  private static final String FRAMEWORK = "net5.0";

  @Test
  public void frameworkOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setFramework(PublishTests.FRAMEWORK);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "-f:" + PublishTests.FRAMEWORK);
      }
      {
        final Publish command = new Publish();
        command.setFramework("  ");
        steps.add(command);
        clc.expectCommand().withArguments("publish");
      }
    });
  }

  private static final String[] MANIFESTS = {
    "/path/to/first/manifest",
    "E:\\Second\\Manifest"
  };

  @Test
  public void manifestOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setManifest(PublishTests.MANIFESTS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--manifest", PublishTests.MANIFESTS[0]);
      }
      {
        final Publish command = new Publish();
        command.setManifest(" ");
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
      {
        final Publish command = new Publish();
        command.setManifest(null);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  @Test
  public void manifestsOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setManifests(PublishTests.MANIFESTS);
        steps.add(command);
        {
          final Stream<String> manifests = Stream.of(PublishTests.MANIFESTS).flatMap(s -> Stream.of("--manifest", s));
          clc.expectCommand().withArgument("publish").withArguments(manifests);
        }
      }
      {
        final Publish command = new Publish();
        command.setManifests(PublishTests.MANIFESTS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--manifest", PublishTests.MANIFESTS[0]);
      }
      {
        final Publish command = new Publish();
        command.setManifests("", "  ", null);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
      {
        final Publish command = new Publish();
        command.setManifests((String[]) null);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  @Test
  public void manifestsStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setManifestsString(String.join(" ", PublishTests.MANIFESTS));
        steps.add(command);
        {
          final Stream<String> manifests = Stream.of(PublishTests.MANIFESTS).flatMap(s -> Stream.of("--manifest", s));
          clc.expectCommand().withArgument("publish").withArguments(manifests);
        }
      }
      {
        final Publish command = new Publish();
        command.setManifestsString(PublishTests.MANIFESTS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--manifest", PublishTests.MANIFESTS[0]);
      }
      {
        final Publish command = new Publish();
        command.setManifestsString(null);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  @Test
  public void noBuildFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setNoBuild(true);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--no-build");
      }
      {
        final Publish command = new Publish();
        command.setNoBuild(false);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  @Test
  public void noDependenciesFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setNoDependencies(true);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--no-dependencies");
      }
      {
        final Publish command = new Publish();
        command.setNoDependencies(false);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  @Test
  public void noRestoreFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setNoRestore(true);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--no-restore");
      }
      {
        final Publish command = new Publish();
        command.setNoRestore(false);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  private static final String RUNTIME = "debian-x64";

  @Test
  public void runtimeOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setRuntime(PublishTests.RUNTIME);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "-r:" + PublishTests.RUNTIME);
      }
      {
        final Publish command = new Publish();
        command.setRuntime(null);
        steps.add(command);
        clc.expectCommand().withArguments("publish");
      }
    });
  }

  @Test
  public void selfContainedFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setSelfContained(true);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--self-contained", "true");
      }
      {
        final Publish command = new Publish();
        command.setSelfContained(false);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--self-contained", "false");
      }
      {
        final Publish command = new Publish();
        command.setSelfContained(null);
        steps.add(command);
        clc.expectCommand().withArgument("publish");
      }
    });
  }

  private static final String VERSION_SUFFIX = "unit.test";

  @Test
  public void versionSuffixOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Publish command = new Publish();
        command.setVersionSuffix(PublishTests.VERSION_SUFFIX);
        steps.add(command);
        clc.expectCommand().withArguments("publish", "--version-suffix", PublishTests.VERSION_SUFFIX);
      }
      {
        final Publish command = new Publish();
        command.setVersionSuffix(null);
        steps.add(command);
        clc.expectCommand().withArguments("publish");
      }
    });
  }

}
