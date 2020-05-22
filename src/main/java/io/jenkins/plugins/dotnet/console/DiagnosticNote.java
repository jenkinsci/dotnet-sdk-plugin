package io.jenkins.plugins.dotnet.console;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.MarkupText;
import hudson.console.ConsoleAnnotationDescriptor;
import hudson.console.ConsoleNote;
import org.jenkinsci.Symbol;

public final class DiagnosticNote extends ConsoleNote<Object> {

  private static final DiagnosticAnnotator ANNOTATOR = new DiagnosticAnnotator();

  @Override
  public DiagnosticAnnotator annotate(@NonNull Object context, MarkupText text, int charPos) {
    final DiagnosticAnnotator annotator = DiagnosticNote.ANNOTATOR;
    annotator.annotate(context, text);
    return annotator;
  }

  @Extension
  @Symbol("dotnetDiagnostic")
  public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {

    public String getDisplayName() {
      return ".NET Diagnostic Messages";
    }

  }

}
