package org.study.juli.logging.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.study.juli.logging.queue.StudyHandler;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class WorkerStudyContextImpl extends AbstractStudyContext implements WorkerContext {

  /** . */
  private final ExecutorService executorService;
  /** . */
  private final ScheduledExecutorService scheduledExecutorService;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param executorService 执行任务的线程池.
   * @param scheduledExecutorService 定时调度的线程池.
   * @author admin
   */
  public WorkerStudyContextImpl(
      final ExecutorService executorService,
      final ScheduledExecutorService scheduledExecutorService) {
    this.executorService = executorService;
    this.scheduledExecutorService = scheduledExecutorService;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void executeInExecutorService(final Runnable task) {
    executorService.execute(task);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public <T> void executeInExecutorService(final T event, final StudyHandler<T> handler) {
    Runnable task = () -> dispatch(event, handler);
    executorService.submit(task);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public ScheduledExecutorService getScheduledExecutorService() {
    return scheduledExecutorService;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param unique .
   * @param event .
   * @param handler .
   * @author admin
   */
  @Override
  public <T> void executeInExecutorServiceV2(
      final String unique, final T event, final StudyHandler<T> handler) {
    Runnable task = () -> dispatchV2(unique, event, handler);
    executorService.submit(task);
  }
}
