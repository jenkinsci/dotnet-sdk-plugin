package io.jenkins.plugins.dotnet;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.*;

/**
 * A (meta)step that executes a .NET command.
 * <p>
 * Automatically wraps the builders deriving from {@link DotNet} and executes them, passing along the environment variables, unlike
 * the CoreStep/SimpleBuildStep combo (pending <a href="https://issues.jenkins-ci.org/browse/JENKINS-29144">JENKINS-29144</a>
 * anyway).
 */
public final class DotNetStep extends Step {

  private final DotNet builder;

  /** Creates a new step, wrapping the specific .NET builder. */
  @DataBoundConstructor
  @SuppressWarnings("unused")
  public DotNetStep(@Nonnull DotNet builder) {
    this.builder = builder;
  }

  @Nonnull
  @Override
  public StepExecution start(@Nonnull StepContext context) {
    return new DotNetStep.Execution(this.builder, context);
  }

  private static final class Execution extends SynchronousNonBlockingStepExecution<Void> {

    private static final long serialVersionUID = -1006932277774627110L;

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final DotNet builder;

    Execution(@Nonnull DotNet builder, @Nonnull StepContext context) {
      super(context);
      this.builder = builder;
    }

    /**
     * Runs the stored .NET builder as a step.
     * <p>
     * This gets the required information (run, working directory, environment variables, launcher and listener) from the step
     * context and passes them along to {@link DotNet#run(FilePath, EnvVars, Launcher, TaskListener, Charset)}.
     *
     * @return Nothing.
     * @throws Exception When the working directory did not exist and could not be created, or when
     *                   {@link DotNet#run(FilePath, EnvVars, Launcher, TaskListener, Charset)} threw an exception.
     */
    @Override
    protected Void run() throws Exception {
      final Run<?, ?> run = Objects.requireNonNull(this.getContext().get(Run.class));
      final FilePath workspace = Objects.requireNonNull(this.getContext().get(FilePath.class));
      workspace.mkdirs();
      final EnvVars environment = Objects.requireNonNull(this.getContext().get(EnvVars.class));
      final Launcher launcher = Objects.requireNonNull(this.getContext().get(Launcher.class));
      final TaskListener listener = Objects.requireNonNull(this.getContext().get(TaskListener.class));
      final Result r = this.builder.run(workspace, environment, launcher, listener, run.getCharset());
      if (r != Result.SUCCESS)
        run.setResult(r);
      return null;
    }

    @Nonnull
    @Override
    public String getStatus() {
      final String supe = super.getStatus();
      return this.builder != null ? this.builder.getClass().getName() + ": " + supe : supe;
    }

  }

  @Extension
  public static final class DescriptorImpl extends StepDescriptor {

    @Nonnull
    @Override
    public String getFunctionName() {
      return "dotnetStep";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return ".NET Step";
    }

    @Override
    public boolean isMetaStep() {
      return true;
    }

    /**
     * Gets the descriptors to which this metastep applies.
     *
     * @return The {@link BuildStepDescriptor}s for the builders deriving from {@link DotNet}. Uses a dynamic lookup to avoid
     * hard-coding that list.
     */
    @Nonnull
    @SuppressWarnings("unused")
    public Collection<? extends Descriptor<?>> getApplicableDescriptors() {
      final List<Descriptor<?>> r = new ArrayList<>();
      for (final Descriptor<?> d : Jenkins.get().getDescriptorList(Builder.class)) {
        if (DotNet.class.isAssignableFrom(d.clazz))
          r.add(d);
      }
      return r;
    }

    @Nonnull
    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(Run.class, FilePath.class, EnvVars.class, Launcher.class, TaskListener.class);
    }

    // Note: CoreStep also overrides argumentsToString(), but that does not seem to be particularly relevant for DotNet builders.

  }

}
