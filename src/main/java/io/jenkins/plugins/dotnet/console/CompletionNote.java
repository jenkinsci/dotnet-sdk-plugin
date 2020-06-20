package io.jenkins.plugins.dotnet.console;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.MarkupText;
import hudson.console.ConsoleAnnotationDescriptor;
import hudson.console.ConsoleAnnotator;
import hudson.console.ConsoleNote;
import org.jenkinsci.Symbol;

/** A note inserted in the "command completed" line of output; applies styling. */
public final class CompletionNote extends ConsoleNote<Object> {

  private static final long serialVersionUID = -203953808379356595L;

  @SuppressWarnings("rawtypes")
  @Override
  public ConsoleAnnotator annotate(@Nullable Object context, @NonNull MarkupText text, int charPos) {
    final String t = text.getText().replaceAll("\r?\n$", "");
    text.addMarkup(0, text.length(), "<span class='dotnet-completed-line'>", "</span><br/>");
    text.addMarkup(charPos, t.length(), "<span class='dotnet-exit-code'>", "</span>");
    return null;
  }

  /** Descriptor for {@link CompletionNote}. */
  @Extension
  @Symbol("dotnetCommandCompleted")
  public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {

    @Override
    @NonNull
    public String getDisplayName() {
      return ".NET Commmand Completion Message";
    }

  }

}
