package org.study.juli.logging.pressure.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.study.juli.logging.base.Constants;

/**
 * 线程池任务队列背压策略.
 *
 * <p>当前策略会阻塞上游线程,并利用上游线程执行当前任务.
 *
 * @author admin
 */
public class StudyRejectedPolicy implements RejectedExecutionHandler {

  /**
   * A handler for rejected tasks that runs the rejected task directly in the calling thread of the
   * execute method, unless the executor has been shut down, in which case the task is discarded.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private final ReentrantLock lock = new ReentrantLock();

  private final Condition condition = lock.newCondition();

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    // 不在主线运行任务,但是可以在主线程上阻塞,将任务重新加入到队列.
    BlockingQueue<Runnable> queue = e.getQueue();
    while (!queue.offer(r)) {
      lock.lock();
      try {
        if (condition.await(Constants.LOOP_COUNT, TimeUnit.MILLISECONDS)) {
          //
        }
      } catch (InterruptedException interruptedException) {
        Thread.currentThread().interrupt();
      } finally {
        lock.unlock();
      }
    }
  }
}
