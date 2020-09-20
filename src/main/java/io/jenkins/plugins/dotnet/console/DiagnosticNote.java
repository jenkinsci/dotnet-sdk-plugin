package io.jenkins.plugins.dotnet.console;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.MarkupText;
import hudson.console.ConsoleAnnotationDescriptor;
import hudson.console.ConsoleAnnotator;
import hudson.console.ConsoleNote;
import org.jenkinsci.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A console note that applies styling to diagnostic messages (warnings and errors) in the output of .NET commands.
 * <p>
 * The detection is not exhaustive since not all .NET commands use clear markers for these messages. In addition, it currently uses
 * hardcoded 'error' and 'warning' strings, so if the node has a non-English language configured, the decoration will likely fail.
 * <p>
 * A better approach might be to create a custom MSBuild logger which would emit special markers, which this scanner could then
 * replace by the appropriate code. That would even allow clear marking of other things, like targets being executed.
 * However, the question then is how to get and where to store that logger; plus, it needs to be specifically activated, so it would
 * only be used by the builders, not the wrapper.
 * <p>
 * In the shorter term, it may be enough to have a few well-known alternatives for 'error' and 'warning', but assumptions about
 * message format and word order may still cause problems.
 */
public final class DiagnosticNote extends ConsoleNote<Object> {

  private static final long serialVersionUID = 6450543178454017373L;

  /** The URL prefix used for error codes whose prefix is not present in {@link #MESSAGE_PREFIX_URLS}. */
  private static final String GENERIC_MESSAGE_URL_BASE = "https://www.google.com/search?q=";

  /** Map linking a message prefix to a URL prefix documenting the message. */
  // FIXME: In Java 11, this could use Map.of().
  private static final Map<String, String> MESSAGE_PREFIX_URLS = new HashMap<>();

  // TODO: Maybe make this some sort of global configuration?

  static {
    DiagnosticNote.MESSAGE_PREFIX_URLS.put("CS", "https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/compiler-messages/"); // C#
    DiagnosticNote.MESSAGE_PREFIX_URLS.put("FS", "https://docs.microsoft.com/en-us/dotnet/fsharp/language-reference/compiler-messages/"); // F#
    DiagnosticNote.MESSAGE_PREFIX_URLS.put("NU", "https://docs.microsoft.com/en-us/nuget/reference/errors-and-warnings/"); // NuGet
  }

  /** Regular expression for the 'error' label. */
  private static final String RE_ERROR = "error";

  /** Regular expression for the 'warning' label. */
  private static final String RE_WARNING = "warning";

  /**
   * Regular expression for the start of a diagnostic message (i.e. before the 'error' or 'warning').
   * <p>
   * This includes the first capture group (the 'context'; usually a file name, or "MSBUILD" for top-level MSBuild errors) and
   * initiates the second one (for the message keyword and code).
   */
  private static final String RE_START = "^(?:(.*):)? *(";

  /**
   * Regular expression for the end of a diagnostic message (i.e. after the 'error' or 'warning').
   * <p>
   * This includes the third and fourth capture group (the message code prefix and number, respectively), ends the second one, and
   * then includes the fifth and final capture group (the current build file).
   */
  private static final String RE_END = " *(?: *([A-Z]+)([0-9]+)| [^ :]+)? *:).*?( +\\[.*])?$";

  /** Regular expression pattern for an error line. */
  private static final Pattern RE_ERROR_LINE = Pattern.compile("" + DiagnosticNote.RE_START + DiagnosticNote.RE_ERROR + DiagnosticNote.RE_END, Pattern.CASE_INSENSITIVE);

  /** Regular expression pattern for a warning line. */
  private static final Pattern RE_WARNING_LINE = Pattern.compile("" + DiagnosticNote.RE_START + DiagnosticNote.RE_WARNING + DiagnosticNote.RE_END, Pattern.CASE_INSENSITIVE);

  /** Regular expression pattern for a diagnostic line (error or warning). */
  private static final Pattern RE_DIAGNOSTIC_LINE = Pattern.compile("" + DiagnosticNote.RE_START + "(?:" + DiagnosticNote.RE_ERROR + "|" + DiagnosticNote.RE_WARNING + ")" + DiagnosticNote.RE_END, Pattern.CASE_INSENSITIVE);

  @SuppressWarnings("rawtypes")
  @Override
  public ConsoleAnnotator annotate(@NonNull Object context, MarkupText text, int charPos) {
    // FIXME: This logic should probably avoid adding markup to a line that already includes it.
    final String t = text.getText().replaceAll("\r?\n$", "");
    {
      Matcher m = DiagnosticNote.RE_ERROR_LINE.matcher(t);
      final String htmlClass;
      final char icon;
      if (m.matches()) {
        htmlClass = "dotnet-error-line";
        icon = '⛔';
      }
      else {
        m = DiagnosticNote.RE_WARNING_LINE.matcher(t);
        if (m.matches()) {
          htmlClass = "dotnet-warning-line";
          icon = '⚠';
        }
        else
          return null;
      }
      {
        final int buildFileStart = m.start(5);
        final int len = buildFileStart >= 0 ? buildFileStart : t.length();
        text.addMarkup(0, len, icon + "<span class='" + htmlClass + "'>", "</span>");
      }
      if (m.start(1) >= 0)
        text.addMarkup(m.start(1), m.end(1), "<span class='dotnet-message-context'>", "</span>");
      if (m.start(2) >= 0) {
        final String prefix = m.group(3);
        final String number = m.group(4);
        if (prefix != null) {
          String url = DiagnosticNote.MESSAGE_PREFIX_URLS.get(prefix);
          if (url == null)
            url = DiagnosticNote.GENERIC_MESSAGE_URL_BASE + prefix + number;
          else
            url += prefix.toLowerCase() + number;
          text.addHyperlinkLowKey(m.start(2), m.end(2), url);
        }
      }
    }
    return null;
  }

  /**
   * Scans a line of text to determine whether it would be styled by this note.
   *
   * @param text The text to scan.
   *
   * @return {@code true} if {@code text} contains a diagnostic line; {@code false} otherwise.
   */
  public static boolean appliesTo(@NonNull String text) {
    return DiagnosticNote.RE_DIAGNOSTIC_LINE.matcher(text).matches();
  }

  /** Descriptor for {@link DiagnosticNote}. */
  @Extension
  @Symbol("dotnetDiagnostic")
  public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return ".NET Diagnostic Messages";
    }

  }

}
