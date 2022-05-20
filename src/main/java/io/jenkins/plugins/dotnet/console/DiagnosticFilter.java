package io.jenkins.plugins.dotnet.console;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.console.ConsoleLogFilter;
import hudson.model.Run;
import io.jenkins.plugins.dotnet.DotNetUtils;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/** A console log filter for .NET diagnostic messages. */
public final class DiagnosticFilter extends ConsoleLogFilter implements Serializable {

  private static final long serialVersionUID = 5093823694192317972L;

  /** An encoded diagnostic note. */
  private final byte[] diagnosticNote = DotNetUtils.encodeNote(DiagnosticNote::new);

  /**
   * Creates a {@link DiagnosticScanner} to preprocess output going to the specified stream.
   *
   * @param build  The active build. Not used.
   * @param logger The stream to decorate.
   *
   * @return The created {@link DiagnosticScanner}.
   */
  @Override
  @NonNull
  public OutputStream decorateLogger(@Nullable Run build, @NonNull OutputStream logger) {
    if (logger instanceof DiagnosticScanner)
      return logger;
    return new DiagnosticScanner(logger, StandardCharsets.UTF_8, this.diagnosticNote);
  }

}
