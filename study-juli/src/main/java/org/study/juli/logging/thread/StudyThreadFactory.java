package org.study.juli.logging.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.study.juli.logging.monitor.Monitor;
import org.study.juli.logging.monitor.ThreadMonitor;

/**
 * 线程工厂,主要是用来创建线程,提供给系统底层.
 *
 * <p>线程工厂,主要是用来创建线程,提供给系统底层.
 *
 * @author admin
 */
public class StudyThreadFactory implements ThreadFactory {
  /** 线程名前缀. */
  private final String prefix;
  /** 线程名后缀计数. */
  private final AtomicInteger threadCount = new AtomicInteger(0);
  /** 线程监听检查对象. */
  private final Monitor checker;
  /** 线程的类型0和1. */
  private final int threadType;
  /** 线程运行的最大执行时间. */
  private final long maxExecTime;
  /** 线程运行的最大执行时间单位. */
  private final TimeUnit maxExecTimeUnit;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param prefixParam 线程名前缀.
   * @param checkerParam 线程监听检查对象.
   * @author admin
   */
  public StudyThreadFactory(final String prefixParam, final Monitor checkerParam) {
    // 线程名前缀.
    this.prefix = prefixParam;
    // 线程检查对象,线程会注册到上面.
    this.checker = checkerParam;
    // 线程类型默认是0,工作现场.
    this.threadType = 0;
    // 线程允许的最大执行时间是10秒.
    this.maxExecTime = Constants.MAX_EXEC_TIME;
    // 线程允许的最大执行时间单位是毫秒.
    this.maxExecTimeUnit = TimeUnit.MILLISECONDS;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param prefixParam 线程名前缀.
   * @param checkerParam 线程监听检查对象.
   * @param threadTypeParam .
   * @param maxExecTimeParam .
   * @param maxExecTimeUnitParam .
   * @author admin
   */
  public StudyThreadFactory(
      final String prefixParam,
      final ThreadMonitor checkerParam,
      final int threadTypeParam,
      final long maxExecTimeParam,
      final TimeUnit maxExecTimeUnitParam) {
    this.prefix = prefixParam;
    this.checker = checkerParam;
    this.threadType = threadTypeParam;
    this.maxExecTime = maxExecTimeParam;
    this.maxExecTimeUnit = maxExecTimeUnitParam;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param runnable 任务.
   * @return Thread 返回一个StudyThread线程.
   * @author admin
   */
  @Override
  public Thread newThread(final Runnable runnable) {
    // 创建线程,由线程工厂触发.
    StudyThread t =
        new StudyThread(
            runnable,
            prefix + threadCount.getAndIncrement(),
            threadType,
            maxExecTime,
            maxExecTimeUnit);
    // 如果线程检查对象不为空.
    if (checker != null) {
      // 注册线程.
      checker.registerThread(t);
    }
    // 设置线程为守护线程.不能阻止外部调用程序jvm退出.
    t.setDaemon(true);
    return t;
  }
}
