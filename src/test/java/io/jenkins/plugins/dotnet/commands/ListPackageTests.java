package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

import java.util.stream.Stream;

public final class ListPackageTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new ListPackage());
      clc.expectCommand().withArguments("list", "package");
    });
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final ListPackage command = new ListPackage();
      command.setProject(ListPackageTests.PROJECT);
      steps.add(command);
      clc.expectCommand().withArguments("list", ListPackageTests.PROJECT, "package");
    });
  }

  private static final String CONFIG_FILE_NAME = "packages.config";

  @Test
  public void configOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--deprecated", "--config", ListPackageTests.CONFIG_FILE_NAME);
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--config", ListPackageTests.CONFIG_FILE_NAME);
      }
      {
        final ListPackage command = new ListPackage();
        command.setVulnerable(true);
        command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--vulnerable", "--config", ListPackageTests.CONFIG_FILE_NAME);
      }
      {
        final ListPackage command = new ListPackage();
        command.setConfig(null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void deprecatedFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--deprecated");
      }
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  private static final String[] FRAMEWORKS = {
    "net472",
    "netstandard2.0",
    "netcoreapp3.1"
  };

  @Test
  public void frameworkOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setFramework(ListPackageTests.FRAMEWORKS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--framework", ListPackageTests.FRAMEWORKS[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setFramework("    ");
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setFramework(null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void frameworksOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setFrameworks(ListPackageTests.FRAMEWORKS);
        steps.add(command);
        {
          final Stream<String> frameworks = Stream.of(ListPackageTests.FRAMEWORKS).flatMap(s -> Stream.of("--framework", s));
          clc.expectCommand().withArguments("list", "package").withArguments(frameworks);
        }
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworks(ListPackageTests.FRAMEWORKS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--framework", ListPackageTests.FRAMEWORKS[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworks("", null, "");
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworks("   ");
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworks((String[]) null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void frameworksStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setFrameworksString(String.join(" ", ListPackageTests.FRAMEWORKS));
        steps.add(command);
        {
          final Stream<String> frameworks = Stream.of(ListPackageTests.FRAMEWORKS).flatMap(s -> Stream.of("--framework", s));
          clc.expectCommand().withArguments("list", "package").withArguments(frameworks);
        }
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworksString(ListPackageTests.FRAMEWORKS[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--framework", ListPackageTests.FRAMEWORKS[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworksString("   ");
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setFrameworksString(null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void highestMinorFlagWorks() throws Exception {
    super.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setHighestMinor(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--highest-minor");
      }
      {
        final ListPackage command = new ListPackage();
        command.setHighestMinor(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setHighestMinor(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void highestPatchFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setHighestPatch(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--highest-patch");
      }
      {
        final ListPackage command = new ListPackage();
        command.setHighestPatch(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setHighestPatch(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void includePrereleaseFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setIncludePrerelease(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--include-prerelease");
      }
      {
        final ListPackage command = new ListPackage();
        command.setIncludePrerelease(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setIncludePrerelease(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void includeTransitiveFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setIncludeTransitive(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--include-transitive");
      }
      {
        final ListPackage command = new ListPackage();
        command.setIncludeTransitive(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void outdatedFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated");
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  private static final String[] SOURCES = {
    "nuget.org",
    "GitHub Packages"
  };

  private static final String SOURCES_STRING = "nuget.org 'GitHub Packages'";

  @Test
  public void sourceOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        command.setSource(ListPackageTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSource(ListPackageTests.SOURCES[1]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--source", ListPackageTests.SOURCES[1]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setVulnerable(true);
        command.setSource(ListPackageTests.SOURCES_STRING);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--vulnerable", "--source", ListPackageTests.SOURCES_STRING);
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSource("  ");
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated");
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSource(null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated");
      }
      {
        final ListPackage command = new ListPackage();
        command.setSource(ListPackageTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void sourcesOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        command.setSources(ListPackageTests.SOURCES);
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(ListPackageTests.SOURCES).flatMap(s -> Stream.of("--source", s));
          clc.expectCommand().withArguments("list", "package", "--deprecated").withArguments(sources);
        }
      }
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        command.setSources(ListPackageTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSources(ListPackageTests.SOURCES_STRING);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--source", ListPackageTests.SOURCES_STRING);
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSources("", null, "    ");
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated");
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSources((String[]) null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated");
      }
    });
  }

  @Test
  public void sourcesStringOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        command.setSourcesString(ListPackageTests.SOURCES_STRING);
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(ListPackageTests.SOURCES).flatMap(s -> Stream.of("--source", s));
          clc.expectCommand().withArguments("list", "package", "--deprecated").withArguments(sources);
        }
      }
      {
        final ListPackage command = new ListPackage();
        command.setDeprecated(true);
        command.setSourcesString(ListPackageTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSourcesString(ListPackageTests.SOURCES_STRING);
        steps.add(command);
        {
          final Stream<String> sources = Stream.of(ListPackageTests.SOURCES).flatMap(s -> Stream.of("--source", s));
          clc.expectCommand().withArguments("list", "package", "--outdated").withArguments(sources);
        }
      }
      {
        final ListPackage command = new ListPackage();
        command.setOutdated(true);
        command.setSourcesString(ListPackageTests.SOURCES[0]);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--outdated", "--source", ListPackageTests.SOURCES[0]);
      }
      {
        final ListPackage command = new ListPackage();
        command.setSourcesString(null);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setSourcesString(String.join(" ", ListPackageTests.SOURCES));
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
      {
        final ListPackage command = new ListPackage();
        command.setSourcesString(String.join(" ", ListPackageTests.SOURCES[0]));
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

  @Test
  public void vulnerableFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final ListPackage command = new ListPackage();
        command.setVulnerable(true);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package", "--vulnerable");
      }
      {
        final ListPackage command = new ListPackage();
        command.setVulnerable(false);
        steps.add(command);
        clc.expectCommand().withArguments("list", "package");
      }
    });
  }

}
