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
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A (meta)step that executes a .NET command.
 * <p>
 * Automatically wraps the builders deriving from {@link DotNet} and executes them, passing along the environment variables, unlike
 * the CoreStep/SimpleBuildStep combo (pending <a href="https://issues.jenkins-ci.org/browse/JENKINS-29144">JENKINS-29144</a>
 * anyway).
 */
public final class DotNetStep extends Step {

  /** The wrapped .NET builder. */
  public final DotNet builder;

  /**
   * Creates a new step, wrapping the specified .NET builder.
   *
   * @param builder The .NET builder to wrap as a pipeline step.
   */
  @DataBoundConstructor
  @SuppressWarnings("unused")
  public DotNetStep(@Nonnull DotNet builder) {
    this.builder = builder;
  }

  /**
   * Starts the .NET step execution.
   *
   * @param context The step context.
   *
   * @return A .NET step execution object.
   */
  @Nonnull
  @Override
  public StepExecution start(@Nonnull StepContext context) {
    return new DotNetStep.Execution(this.builder, context);
  }

  /** The execution object for a .NET step. */
  private static final class Execution extends SynchronousNonBlockingStepExecution<Void> {

    private static final long serialVersionUID = -1006932277774627110L;

    /** The builder to execute. */
    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final DotNet builder;

    /**
     * Creates an execution object for a .NET step.
     *
     * @param builder The builder to execute.
     * @param context The step context.
     */
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

  }

  /** The descriptor for the .NET step. */
  @Extension
  public static final class DescriptorImpl extends StepDescriptor {

    /**
     * Gets the function name used to execute the step in a pipeline script.
     *
     * @return The function name.
     */
    @Nonnull
    @Override
    public String getFunctionName() {
      return "dotnetStep";
    }

    /**
     * Gets the display name for the .NET step.
     *
     * @return The display name.
     */
    @Nonnull
    @Override
    public String getDisplayName() {
      return ".NET Step";
    }

    /**
     * Determines whether or not this is a meta-step (a step wrapping other steps).
     *
     * @return {@code true}.
     */
    @Override
    public boolean isMetaStep() {
      return true;
    }

    /**
     * Gets the descriptors to which this meta-step applies.
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

    /**
     * Determines the set of context variables required by this step.
     *
     * @return The types of context variables required by this step.
     */
    @Nonnull
    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(Run.class, FilePath.class, EnvVars.class, Launcher.class, TaskListener.class);
    }

  }

}
