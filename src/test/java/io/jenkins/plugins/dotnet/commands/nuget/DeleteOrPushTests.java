package io.jenkins.plugins.dotnet.commands.nuget;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.util.Secret;
import io.jenkins.plugins.dotnet.commands.CommandTests;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Test;

public final class DeleteOrPushTests extends CommandTests {

  @Test
  public void simpleExecutionWorks() throws Exception {
    // same as a plain NuGetCommand
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      steps.add(new NuGetCommand());
      clc.expectCommand().withArguments("nuget");
    });
  }

  private static final String API_KEY_ID = "nuget-api-key";

  private static final String API_KEY_VALUE = "super seekrit key";

  private static final String SOURCE = "nuget.jenkins.org";

  @Test
  public void apiKeyOptionWorks() throws Exception {
    final CredentialsScope scope = CredentialsScope.GLOBAL;
    final String description = "NuGet API Key";
    final Secret secret = Secret.fromString(DeleteOrPushTests.API_KEY_VALUE);
    final StandardCredentials apiKey = new StringCredentialsImpl(scope, DeleteOrPushTests.API_KEY_ID, description, secret);
    super.withCredentials(apiKey, () -> super.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final DeleteOrPush command = new DeleteOrPush();
      command.setApiKeyId(DeleteOrPushTests.API_KEY_ID);
      steps.add(command);
      clc.expectCommand().withArguments("nuget", "--api-key").withArgument(DeleteOrPushTests.API_KEY_VALUE, true);
    }));
  }

  @Test
  public void noServiceEndpointFlagWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      {
        final DeleteOrPush command = new DeleteOrPush();
        command.setNoServiceEndpoint(true);
        steps.add(command);
        clc.expectCommand().withArguments("nuget", "--no-service-endpoint");
      }
      {
        final DeleteOrPush command = new DeleteOrPush();
        command.setNoServiceEndpoint(false);
        steps.add(command);
        clc.expectCommand().withArguments("nuget");
      }
    });
  }

  @Test
  public void sourceOptionWorks() throws Exception {
    this.runCommandsAndValidateProcessExecution((steps, clc) -> {
      final DeleteOrPush command = new DeleteOrPush();
      command.setSource(DeleteOrPushTests.SOURCE);
      steps.add(command);
      clc.expectCommand().withArguments("nuget", "--source", DeleteOrPushTests.SOURCE);
    });
  }

}
