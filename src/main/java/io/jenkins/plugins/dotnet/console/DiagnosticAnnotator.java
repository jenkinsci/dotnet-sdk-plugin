package io.jenkins.plugins.dotnet.console;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.MarkupText;
import hudson.console.ConsoleAnnotator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An annotator that adds markup for diagnostics (mostly errors and warnings).
 */
public final class DiagnosticAnnotator extends ConsoleAnnotator<Object> {

  /** The URL prefix used for error codes whose prefix is not present in {@link #MESSAGE_PREFIX_URLS}. */
  private static final String GENERIC_MESSAGE_URL_BASE = "https://www.google.com/search?q=";

  /** Map linking a message prefix to a URL prefix documenting the message. */
  // FIXME: In Java 11, this could use Map.of().
  private static final Map<String, String> MESSAGE_PREFIX_URLS = new HashMap<>();

  static {
    MESSAGE_PREFIX_URLS.put("CS", "https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/compiler-messages/"); // C#
    MESSAGE_PREFIX_URLS.put("FS", "https://docs.microsoft.com/en-us/dotnet/fsharp/language-reference/compiler-messages/"); // F#
    MESSAGE_PREFIX_URLS.put("NU", "https://docs.microsoft.com/en-us/nuget/reference/errors-and-warnings/"); // NuGet
  }

  // FIXME: This will fail for non-English environments.
  // FIXME: A better approach might be to create a custom MSBuild logger which would emit special marker lines, which we then
  // FIXME: replace by the appropriate note.
  // FIXME: That could even allow marking all targets being executed, adding an overview at the end, including links to their
  // FIXME: output (would only happen at an appropriate verbosity level).

  // FIXME: In the shorter term, it may be enough to have a few well-known alternatives for 'error' and 'warning', but assumptions
  // FIXME: about message format and word order will still cause problems.

  /**
   * Regular expression for the start of a diagnostic message (i.e. before the 'error' or 'warning').
   * <p>
   * This includes the first capture group (the 'context'; usually a file name, or "MSBUILD" for top-level MSBuild errors) and
   * initiates the second one (for the message keyword and code).
   */
  private static final String RE_START = "^(?:(.*):)? *(";

  /** Regular expression for the 'error' label. */
  private static final String RE_ERROR = "error";

  /** Regular expression for the 'warning' label. */
  private static final String RE_WARNING = "warning";

  /**
   * Regular expression for the end of a diagnostic message (i.e. after the 'error' or 'warning').
   * <p>
   * This includes the third and fourth capture group (the message code prefix and number, respectively), ends the second one, and
   * then includes the fifth and final capture group (the current build file).
   */
  private static final String RE_END = " *(?: *([A-Z]+)([0-9]+)| [^ ]+)? *:).*?( +\\[.*])?$";

  /** Regular expression pattern for an error line. */
  private static final Pattern RE_ERROR_LINE = Pattern.compile("" + RE_START + RE_ERROR + RE_END, Pattern.CASE_INSENSITIVE);

  /** Regular expression pattern for a warning line. */
  private static final Pattern RE_WARNING_LINE = Pattern.compile("" + RE_START + RE_WARNING + RE_END, Pattern.CASE_INSENSITIVE);

  @Override
  public ConsoleAnnotator<Object> annotate(@NonNull Object context, @NonNull MarkupText text) {
    final String t = text.getText().replaceAll("\r?\n$", "");
    if (t.startsWith(".NET Command Completed - Exit Code: ")) {
      text.addMarkup(0, t.length(), "<span class='dotnet-completed-line'>", "</span><br/>");
      text.addMarkup(25, t.length(), "<span class='dotnet-exit-code'>", "</span>");
      return null;
    }
    {
      Matcher m = RE_ERROR_LINE.matcher(t);
      final String htmlClass;
      final char icon;
      if (m.matches()) {
        htmlClass = "dotnet-error-line";
        icon = '⛔';
      }
      else {
        m = RE_WARNING_LINE.matcher(t);
        if (m.matches()) {
          htmlClass = "dotnet-warning-line";
          icon = '⚠';
        }
        else
          return this;
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
          String url = MESSAGE_PREFIX_URLS.get(prefix);
          if (url == null)
            url = GENERIC_MESSAGE_URL_BASE + prefix + number;
          else
            url += prefix.toLowerCase() + number;
          text.addHyperlinkLowKey(m.start(2), m.end(2), url);
        }
      }
    }
    return this;
  }

}
