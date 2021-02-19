package io.jenkins.plugins.dotnet.commands.nuget;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.util.Secret;
import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Test;

public final class PushTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new Push());
      clc.expectCommand().withArguments("nuget", "push");
    });
  }

  private static final String ROOT = "path/to/Some.Package.42.666.nupkg";

  @Test
  public void normalExecutionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Push command = new Push();
      command.setRoot(PushTests.ROOT);
      steps.add(command);
      clc.expectCommand().withArguments("nuget", "push", PushTests.ROOT);
    });
  }

  private static final String SYMBOL_API_KEY_ID = "nuget-symbol-api-key";

  private static final String SYMBOL_API_KEY_VALUE = "super seekrit debugging key";

  private static final String SYMBOL_SOURCE = "symbols.nuget.jenkins.org";

  @Test
  public void disableBufferingFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Push command = new Push();
        command.setDisableBuffering(true);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push", "--disable-buffering");
      }
      {
        final Push command = new Push();
        command.setDisableBuffering(false);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push");
      }
    });
  }

  @Test
  public void noSymbolsFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Push command = new Push();
        command.setNoSymbols(true);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push", "--no-symbols");
      }
      {
        final Push command = new Push();
        command.setNoSymbols(false);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push");
      }
    });
  }

  @Test
  public void skipDuplicateFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Push command = new Push();
        command.setSkipDuplicate(true);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push", "--skip-duplicate");
      }
      {
        final Push command = new Push();
        command.setSkipDuplicate(false);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push");
      }
    });
  }

  @Test
  public void symbolApiKeyOptionWorks() throws Exception {
    final CredentialsScope scope = CredentialsScope.GLOBAL;
    final String description = "NuGet Symbol API Key";
    final Secret secret = Secret.fromString(PushTests.SYMBOL_API_KEY_VALUE);
    final StandardCredentials apiKey = new StringCredentialsImpl(scope, PushTests.SYMBOL_API_KEY_ID, description, secret);
    super.withCredentials(apiKey, () -> super.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Push command = new Push();
      command.setSymbolApiKeyId(PushTests.SYMBOL_API_KEY_ID);
      steps.add(command);
      clc.expectCommand().withArguments("nuget", "push", "--symbol-api-key").withArgument(PushTests.SYMBOL_API_KEY_VALUE, true);
    }));
  }

  @Test
  public void symbolSourceOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final Push command = new Push();
      command.setSymbolSource(PushTests.SYMBOL_SOURCE);
      steps.add(command);
      clc.expectCommand().withArguments("nuget", "push", "--symbol-source", PushTests.SYMBOL_SOURCE);
    });
  }

  @Test
  public void timeoutOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final Push command = new Push();
        command.setTimeout(13);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push", "--timeout", "13");
      }
      {
        final Push command = new Push();
        command.setTimeout(null);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "push");
      }
    });
  }

}
