package io.jenkins.plugins.dotnet;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.console.LineTransformationOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DotNetConsoleProcessor extends LineTransformationOutputStream {

  private final OutputStream out;
  private final Charset charset;

  public DotNetConsoleProcessor(@NonNull OutputStream out, @NonNull Charset charset) {
    this.out = out;
    this.charset = charset;
  }

  @Override
  public void close() throws IOException {
    super.close();
    out.close();
  }

  // FIXME: This will fail for non-English installs. A better approach would be to create a custom MSBuild logger (deployment method
  // FIXME: TBD), we could emit special invariant markers to pick up here, then pass the message along to the standard logger.
  private static final Pattern RE_ERROR = Pattern.compile(": error [A-Z]+[0-9]+:", Pattern.CASE_INSENSITIVE);
  private static final Pattern RE_WARNING = Pattern.compile(": warning [A-Z]+[0-9]+:", Pattern.CASE_INSENSITIVE);

  @Override
  protected void eol(byte[] lineBytes, int lineLength) throws IOException {
    final String line = this.trimEOL(this.charset.decode(ByteBuffer.wrap(lineBytes, 0, lineLength)).toString());
    {
      Matcher m = DotNetConsoleProcessor.RE_ERROR.matcher(line);
      if (m.matches())
        ++this.errors;
      else {
        m = DotNetConsoleProcessor.RE_WARNING.matcher(line);
        if (m.matches())
          ++this.warnings;
      }
    }
    // Maybe also detect the final N errors/N warnings lines and use those (likely to be more accurate).
    out.write(lineBytes, 0, lineLength);
  }

  //region Error / Warning Counts

  private int errors = 0;

  public int getErrors() {
    return errors;
  }

  private int warnings = 0;

  public int getWarnings() {
    return this.warnings;
  }

  //endregion

}
