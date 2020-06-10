package io.jenkins.plugins.dotnet.console;

import hudson.console.LineTransformationOutputStream;
import io.jenkins.plugins.dotnet.DotNetUtils;
import io.jenkins.plugins.dotnet.Messages;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scanner to detect diagnostic messages (warnings and errors) in the output of .NET commands.
 * Also sees the error/warning summary lines for MSBuild-based commands and sets properties to the respective counts.
 * <p>
 * It currently uses hardcoded 'Error(s)' and 'Warning(s)' strings, so if the node has a non-English language configured, the
 * processing will likely fail. It may be enough to have a few well-known alternatives for these string, but assumptions about
 * message format and word order may still cause problems.
 */
public final class DiagnosticScanner extends LineTransformationOutputStream {

  /** The output stream being decorated by this scanner. */
  private final OutputStream out;

  /** The character set to use when interpreting output as text. */
  private final Charset charset;

  /** The encoded {@link DiagnosticNote} to use when marking diagnostic lines. */
  private final byte[] diagnosticNote;

  /** The encoded {@link CompletionNote} to use when marking command completion lines. */
  private final byte[] completionNote;

  /**
   * Creates a new scanner.
   *
   * @param out     The output stream to decorate.
   * @param charset The character set in use.
   */
  public DiagnosticScanner(@Nonnull OutputStream out, @Nonnull Charset charset) {
    this(out, charset, DotNetUtils.encodeNote(DiagnosticNote::new), DotNetUtils.encodeNote(CompletionNote::new));
  }

  /**
   * Creates a new scanner.
   *
   * @param out            The output stream to decorate.
   * @param charset        The character set in use.
   * @param diagnosticNote A specific encoded {@link DiagnosticNote} to use.
   */
  DiagnosticScanner(@Nonnull OutputStream out, @Nonnull Charset charset, @Nonnull byte[] diagnosticNote) {
    this(out, charset, diagnosticNote, null);
  }

  /**
   * Creates a new scanner.
   *
   * @param out            The output stream to decorate.
   * @param charset        The character set in use.
   * @param diagnosticNote A specific encoded {@link DiagnosticNote} to use.
   * @param completionNote A specific encoded {@link CompletionNote} to use.
   */
  private DiagnosticScanner(@Nonnull OutputStream out, @Nonnull Charset charset, @Nonnull byte[] diagnosticNote, @CheckForNull byte[] completionNote) {
    this.out = out;
    this.charset = charset;
    this.diagnosticNote = diagnosticNote;
    this.completionNote = completionNote;
  }

  /** The number of errors reported by an MSBuild-based command in its build summary. */
  private int errors = 0;

  /**
   * Gets the number of errors reported by an MSBuild-based command in its build summary.
   *
   * @return The number of errors reported; 0 if no build summary was seen.
   */
  public int getErrors() {
    return this.errors;
  }

  /** The number of warnings reported by an MSBuild-based command in its build summary. */
  private int warnings = 0;

  /**
   * Gets the number of warnings reported by an MSBuild-based command in its build summary.
   *
   * @return The number of warnings reported; 0 if no build summary was seen.
   */
  public int getWarnings() {
    return this.warnings;
  }

  /**
   * Closes this scanner; this forces end-of-line processing and then closes the wrapped output stream.
   *
   * @throws IOException When thrown by either {@link LineTransformationOutputStream#close()} or {@link OutputStream#close()}.
   */
  @Override
  public void close() throws IOException {
    super.close();
    this.out.close();
  }

  /**
   * Flushes the wrapped output stream.
   *
   * @throws IOException When thrown by {@link OutputStream#flush()}.
   */
  @Override
  public void flush() throws IOException {
    this.out.flush();
  }

  /** Regular expression pattern for the MSBuild error count line. */
  private static final Pattern RE_ERROR_COUNT = Pattern.compile("^ *(\\d+) Error\\(s\\)$");

  /** Regular expression pattern for the MSBuild warning count line. */
  private static final Pattern RE_WARNING_COUNT = Pattern.compile("^ *(\\d+) Warning\\(s\\)$");

  /**
   * Scans a line of output, then forwards it to the wrapped output stream.
   *
   * @param lineBytes  The raw line contents, including any line terminator.
   * @param lineLength The length of the line within {@code lineBytes}.
   *
   * @throws IOException When thrown by {@link OutputStream#write(byte[], int, int)}.
   */
  @Override
  protected void eol(byte[] lineBytes, int lineLength) throws IOException {
    final String line = this.trimEOL(this.charset.decode(ByteBuffer.wrap(lineBytes, 0, lineLength)).toString());
    {
      Matcher m = DiagnosticScanner.RE_ERROR_COUNT.matcher(line);
      if (m.matches())
        this.errors = Integer.parseInt(m.group(1));
      m = DiagnosticScanner.RE_WARNING_COUNT.matcher(line);
      if (m.matches())
        this.warnings = Integer.parseInt(m.group(1));
    }
    if (DiagnosticNote.appliesTo(line))
      this.out.write(this.diagnosticNote);
    this.out.write(lineBytes, 0, lineLength);
  }

  /** The marker that a completion message should contain, to indicate where exit code information starts. */
  private static final String COMPLETION_MESSAGE_EXIT_CODE_MARKER = "<!>";

  /**
   * Writes the command completion message to the stream wrapped by this scanner.
   * This uses a specific marker ({@link #COMPLETION_MESSAGE_EXIT_CODE_MARKER}) in order to add separate markup for the message text
   * and the return code information.
   *
   * @param rc The command's return code.
   */
  public void writeCompletionMessage(int rc) {
    try {
      this.forceEol();
      final String message = Messages.DiagnosticScanner_CompletionMessage(rc);
      final String marker = DiagnosticScanner.COMPLETION_MESSAGE_EXIT_CODE_MARKER;
      // FIXME: This assumes the exit code will always be in the second half of the sentence.
      final int idx = message.indexOf(marker);
      if (idx < 0) // just write the entire line
        this.out.write(message.getBytes(this.charset));
      else
        this.out.write(message.substring(0, idx).getBytes(this.charset));
      if (this.completionNote != null)
        this.out.write(this.completionNote);
      if (idx >= 0)
        this.out.write(message.substring(idx + marker.length()).getBytes(this.charset));
      this.out.write(System.lineSeparator().getBytes(this.charset));
    }
    catch (Throwable t) {
      DiagnosticScanner.LOGGER.log(Level.FINE, Messages.DiagnosticScanner_CompletionMessageFailed(), t);
      // the annotator won't stop, but an error serious enough to make that output line fail is going to abort the build anyway
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DiagnosticScanner.class.getName());

}
