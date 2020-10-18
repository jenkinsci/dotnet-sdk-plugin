package io.jenkins.plugins.dotnet.commands;

import org.junit.Test;

import java.util.stream.Stream;

public final class ListPackageTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(ListPackage::new, check -> check.expectCommand().withArguments("list", "package"));
  }

  private static final String PROJECT = "Foo.Bar.sln";

  @Test
  public void normalExecutionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setProject(ListPackageTests.PROJECT);
      return command;
    }, check -> check.expectCommand().withArguments("list", ListPackageTests.PROJECT, "package"));
  }

  private static final String CONFIG_FILE_NAME = "packages.config";

  @Test
  public void configOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--config", ListPackageTests.CONFIG_FILE_NAME));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--config", ListPackageTests.CONFIG_FILE_NAME));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setConfig(null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setConfig(ListPackageTests.CONFIG_FILE_NAME);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void deprecatedFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(false);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  private static final String[] FRAMEWORKS = { "net472", "netstandard2.0", "netcoreapp3.1" };

  @Test
  public void frameworkOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFramework(ListPackageTests.FRAMEWORKS[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--framework", ListPackageTests.FRAMEWORKS[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFramework("    ");
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFramework(null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void frameworksOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworks(ListPackageTests.FRAMEWORKS);
      return command;
    }, check -> {
      final String[] frameworks = Stream.of(ListPackageTests.FRAMEWORKS).flatMap(s -> Stream.of("--framework", s)).toArray(String[]::new);
      check.expectCommand().withArguments("list", "package").withArguments(frameworks);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworks(ListPackageTests.FRAMEWORKS[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--framework", ListPackageTests.FRAMEWORKS[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworks("", null, "");
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworks("   ");
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworks((String[]) null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void frameworksStringOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworksString(String.join(" ", ListPackageTests.FRAMEWORKS));
      return command;
    }, check -> {
      final String[] frameworks = Stream.of(ListPackageTests.FRAMEWORKS).flatMap(s -> Stream.of("--framework", s)).toArray(String[]::new);
      check.expectCommand().withArguments("list", "package").withArguments(frameworks);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworksString(ListPackageTests.FRAMEWORKS[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--framework", ListPackageTests.FRAMEWORKS[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworksString("   ");
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setFrameworksString(null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void highestMinorFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setHighestMinor(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--highest-minor"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setHighestMinor(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--highest-minor"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setHighestMinor(false);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setHighestMinor(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void highestPatchFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setHighestPatch(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--highest-patch"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setHighestPatch(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--highest-patch"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setHighestPatch(false);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setHighestPatch(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void includePrereleaseFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setIncludePrerelease(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--include-prerelease"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setIncludePrerelease(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--include-prerelease"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setIncludePrerelease(false);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setIncludePrerelease(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void includeTransitiveFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setIncludeTransitive(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--include-transitive"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setIncludeTransitive(false);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void outdatedFlagWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(false);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  private static final String[] SOURCES = { "nuget.org", "GitHub Packages" };
  private static final String SOURCES_STRING = "nuget.org 'GitHub Packages'";

  @Test
  public void sourceOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setSource(ListPackageTests.SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setSource(ListPackageTests.SOURCES[1]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[1]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSource(ListPackageTests.SOURCES_STRING);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--source", ListPackageTests.SOURCES_STRING));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSource("  ");
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSource(null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setSource(ListPackageTests.SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

  @Test
  public void sourcesOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setSources(ListPackageTests.SOURCES);
      return command;
    }, check -> {
      final String[] sources = Stream.of(ListPackageTests.SOURCES).flatMap(s -> Stream.of("--source", s)).toArray(String[]::new);
      check.expectCommand().withArguments("list", "package", "--deprecated").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setSources(ListPackageTests.SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSources(ListPackageTests.SOURCES_STRING);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--source", ListPackageTests.SOURCES_STRING));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSources("", null, "    ");
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSources((String[]) null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated"));
  }

  @Test
  public void sourcesStringOptionWorks() throws Exception {
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setSourcesString(ListPackageTests.SOURCES_STRING);
      return command;
    }, check -> {
      final String[] sources = Stream.of(ListPackageTests.SOURCES).flatMap(s -> Stream.of("--source", s)).toArray(String[]::new);
      check.expectCommand().withArguments("list", "package", "--deprecated").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setDeprecated(true);
      command.setSourcesString(ListPackageTests.SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--deprecated", "--source", ListPackageTests.SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSourcesString(ListPackageTests.SOURCES_STRING);
      return command;
    }, check -> {
      final String[] sources = Stream.of(ListPackageTests.SOURCES).flatMap(s -> Stream.of("--source", s)).toArray(String[]::new);
      check.expectCommand().withArguments("list", "package", "--outdated").withArguments(sources);
    });
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setOutdated(true);
      command.setSourcesString(ListPackageTests.SOURCES[0]);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package", "--outdated", "--source", ListPackageTests.SOURCES[0]));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setSourcesString(null);
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setSourcesString(String.join(" ", ListPackageTests.SOURCES));
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
    super.runCommandAndValidateProcessExecution(() -> {
      final ListPackage command = new ListPackage();
      command.setSourcesString(String.join(" ", ListPackageTests.SOURCES[0]));
      return command;
    }, check -> check.expectCommand().withArguments("list", "package"));
  }

}
