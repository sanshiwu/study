package org.study.juli.logging.monitor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.context.WorkerContext;
import org.study.juli.logging.runnable.GuardianConsumerMonitorRunnable;

/**
 * 当日志数据量小的时候,使用定时器将队列中不足5000的日志刷新到磁盘.
 *
 * <p>这个类性能比较差,因为是定时执行的,不是实时处理,会有几秒钟的延迟.
 *
 * <p>需要优化,应该提供一种实时的延迟小的监听机制.
 *
 * @author admin
 */
public class GuardianConsumerMonitor implements Monitor {
  /** 调用任务,优雅关闭时,调用对象shutdown方法. */
  private ScheduledFuture<?> scheduledFuture;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public GuardianConsumerMonitor() {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param context 上下文.
   * @author admin
   */
  @Override
  public void monitor(final WorkerContext context) {
    // 得到上下文中的定时器线程池.
    final ScheduledExecutorService scheduledExecutorService = context.getScheduledExecutorService();
    Runnable runnable = new GuardianConsumerMonitorRunnable(context);
    // 启动定时器,每5s运行一次任务,检查所有线程的运行情况.
    scheduledFuture =
        scheduledExecutorService.scheduleAtFixedRate(
            runnable, Constants.INITIAL_DELAY, Constants.PERIOD, TimeUnit.MILLISECONDS);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void close() {
    // 如果不为空.
    if (scheduledFuture != null) {
      // 尽可能关闭定时任务对象.
      scheduledFuture.cancel(true);
    }
  }
}
