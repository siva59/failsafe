package net.jodah.failsafe;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import net.jodah.failsafe.event.ContextualResultListener;
import net.jodah.failsafe.function.CheckedBiConsumer;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.internal.util.Assert;
import net.jodah.failsafe.util.concurrent.Scheduler;

/**
 * Failsafe execution event listeners.
 * 
 * @author Jonathan Halterman
 * @param <R> result type
 */
public class Listeners<R> {
  /**
   * Called when an execution is aborted.
   */
  public void onAbort(R result, Throwable failure) {
  }

  /**
   * Called when an execution is aborted.
   */
  public void onAbort(R result, Throwable failure, ExecutionContext context) {
  }

  /**
   * Called when an execution is completed.
   */
  public void onComplete(R result, Throwable failure) {
  }

  /**
   * Called when an execution is completed.
   */
  public void onComplete(R result, Throwable failure, ExecutionContext context) {
  }

  /**
   * Called when an execution attempt fails.
   */
  public void onFailedAttempt(R result, Throwable failure) {
  }

  /**
   * Called when an execution attempt fails.
   */
  public void onFailedAttempt(R result, Throwable failure, ExecutionContext context) {
  }

  /**
   * Called when an execution fails and cannot be retried.
   */
  public void onFailure(R result, Throwable failure) {
  }

  /**
   * Called when an execution fails and cannot be retried.
   */
  public void onFailure(R result, Throwable failure, ExecutionContext context) {
  }

  /**
   * Called when an execution fails and retries are exceeded.
   */
  public void onRetriesExceeded(R result, Throwable failure) {
  }

  /**
   * Called before an execution is retried.
   */
  public void onRetry(R result, Throwable failure) {
  }

  /**
   * Called before an execution is retried.
   */
  public void onRetry(R result, Throwable failure, ExecutionContext context) {
  }

  /**
   * Called when an execution is successful.
   */
  public void onSuccess(R result) {
  }

  /**
   * Called when an execution is successful.
   */
  public void onSuccess(R result, ExecutionContext context) {
  }

  @SuppressWarnings("unchecked")
  static <T> ContextualResultListener<T, Throwable> of(
      final ContextualResultListener<? extends T, ? extends Throwable> listener, final ExecutorService executor,
      final Scheduler scheduler) {
    return new ContextualResultListener<T, Throwable>() {
      @Override
      public void onResult(final T result, final Throwable failure, final ExecutionContext context) {
        Callable<T> callable = new Callable<T>() {
          public T call() throws Exception {
            ((ContextualResultListener<T, Throwable>) listener).onResult(result, failure, context);
            return null;
          }
        };

        try {
          if (executor != null)
            executor.submit(callable);
          else
            scheduler.schedule(callable, 0, TimeUnit.MILLISECONDS);
        } catch (Exception ignore) {
        }
      }
    };
  }

  @SuppressWarnings("unchecked")
  static <T> ContextualResultListener<T, Throwable> of(final CheckedConsumer<? extends Throwable> listener) {
    Assert.notNull(listener, "listener");
    return new ContextualResultListener<T, Throwable>() {
      @Override
      public void onResult(T result, Throwable failure, ExecutionContext context) throws Exception {
        ((CheckedConsumer<Throwable>) listener).accept(failure);
      }
    };
  }

  @SuppressWarnings("unchecked")
  static <T> ContextualResultListener<T, Throwable> of(
      final CheckedBiConsumer<? extends T, ? extends Throwable> listener) {
    Assert.notNull(listener, "listener");
    return new ContextualResultListener<T, Throwable>() {
      @Override
      public void onResult(T result, Throwable failure, ExecutionContext context) throws Exception {
        ((CheckedBiConsumer<T, Throwable>) listener).accept(result, failure);
      }
    };
  }

  @SuppressWarnings("unchecked")
  static <T> ContextualResultListener<T, Throwable> ofResult(final CheckedConsumer<? extends T> listener) {
    Assert.notNull(listener, "listener");
    return new ContextualResultListener<T, Throwable>() {
      @Override
      public void onResult(T result, Throwable failure, ExecutionContext context) throws Exception {
        ((CheckedConsumer<T>) listener).accept(result);
      }
    };
  }

  @SuppressWarnings("unchecked")
  static <T> ContextualResultListener<T, Throwable> ofResult(
      final CheckedBiConsumer<? extends T, ExecutionContext> listener) {
    Assert.notNull(listener, "listener");
    return new ContextualResultListener<T, Throwable>() {
      @Override
      public void onResult(T result, Throwable failure, ExecutionContext context) throws Exception {
        ((CheckedBiConsumer<T, ExecutionContext>) listener).accept(result, context);
      }
    };
  }
}
