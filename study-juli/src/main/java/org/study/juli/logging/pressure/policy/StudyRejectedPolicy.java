package org.study.juli.logging.pressure.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.study.juli.logging.utils.LogManagerUtils;

/**
 * 线程池任务队列背压策略.
 *
 * <p>当前策略会阻塞上游线程,并利用上游线程执行当前任务.
 *
 * @author admin
 */
public class StudyRejectedPolicy implements RejectedExecutionHandler {
  /** . */
  private final ReentrantLock lock = new ReentrantLock();
  /** . */
  private final Condition condition = lock.newCondition();

  /**
   * A handler for rejected tasks that runs the rejected task directly in the calling thread of the
   * execute method, unless the executor has been shut down, in which case the task is discarded.
   *
   * <p>Another description after blank line.
   *
   * @param r .
   * @param e .
   * @author admin
   */
  @Override
  public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
    // 如果线程池关闭了,直接返回.
    if (e.isShutdown()) {
      return;
    }
    // 获取日志唯一ID的全局配置.
    final String unique = LogManagerUtils.getProperty(Constants.UNIQUE, Constants.FALSE);
    // 如果日志不需要唯一序列号.
    if (unique.equals(Constants.TRUE)) {
      // 不在主线执行任务,但是可以在主线程上阻塞,将任务重新加入到队列.
      final BlockingQueue<Runnable> queue = e.getQueue();
      // 循环执行,直到返回true为止.
      while (!queue.offer(r) && !Thread.currentThread().isInterrupted()) {
        lock.lock();
        try {
          // 每次等5毫秒.
          if (condition.await(Constants.LOOP_COUNT, TimeUnit.MILLISECONDS)) {
            //
          }
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        } finally {
          lock.unlock();
        }
      }
      // 如果不需要日志唯一消息ID
    } else {
      // 在主线执行任务.背压方式使用主线程执行无法放入队列的任务.
      r.run();
    }
  }
}
