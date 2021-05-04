package org.study.juli.logging.core.context;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.study.juli.logging.api.context.StudyContext;

/**
 * 自定义线程,便于系统内线程的监控.
 *
 * <p>比如设置自定义的线程名,线程计数等.
 *
 * @author admin
 */
public final class StudyThread extends Thread {
  /** 线程类型. */
  private final int threadType;
  /** 线程最大的执行时间. */
  private final long maxExecTime;
  /** 线程最大的执行单位. */
  private final TimeUnit maxExecTimeUnit;
  /** 线程开始运行的时间(毫秒). */
  private long execStart;
  /** 线程运行的上下文环境. */
  private StudyContext context;
  /** 线程从开始到结束之间唯一的UUID. */
  private String unique;

  /**
   * 自定义线程.
   *
   * <p>参数需要加final修饰.
   *
   * @param targetParam 线程任务.
   * @param nameParam 线程名.
   * @param threadTypeParam 线程类型.
   * @param maxExecTimeParam 线程最大执行时间.
   * @param maxExecTimeUnitParam 线程最大执行时间单位.
   * @author admin
   */
  public StudyThread(
      final Runnable targetParam,
      final String nameParam,
      final int threadTypeParam,
      final long maxExecTimeParam,
      final TimeUnit maxExecTimeUnitParam) {
    super(targetParam, nameParam);
    this.threadType = threadTypeParam;
    this.maxExecTime = maxExecTimeParam;
    this.maxExecTimeUnit = maxExecTimeUnitParam;
  }

  /**
   * 返回线程的最大执行时间单位.
   *
   * @return 返回线程的最大执行时间单位.
   * @author admin
   */
  public TimeUnit maxExecTimeUnit() {
    return maxExecTimeUnit;
  }

  /**
   * 返回线程的类型.
   *
   * @return 返回线程的类型.
   * @author admin
   */
  public int threadType() {
    return threadType;
  }

  /**
   * 获取线程运行的开始时间.
   *
   * @return 返回线程的开始运行时间.
   * @author admin
   */
  public long startTime() {
    return execStart;
  }

  /**
   * 获取线程的最大运行时间.
   *
   * @return 返回线程的最大运行时间.
   * @author admin
   */
  public long maxExecTime() {
    return maxExecTime;
  }

  /**
   * 获取线程的上下文对象.
   *
   * @return 返回线程的上习武对象.
   * @author admin
   */
  StudyContext context() {
    return context;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String
   * @author admin
   */
  public String getUnique() {
    return unique;
  }

  /**
   * 动态设置唯一日志消息ID.
   *
   * <p>Another description after blank line.
   *
   * @param unique .
   * @author admin
   */
  public void setUnique(final String unique) {
    this.unique = unique;
  }

  /**
   * 当线程开始时,开始时间设置成当前系统的时间戳毫秒数.
   *
   * @author admin
   */
  private void executeStart() {
    // 如果当前上下文为空.
    if (context == null) {
      // 设置当前系统时间为开始时间,代表线程开始执行.
      execStart = System.currentTimeMillis();
      // 设置线程开始运行时的唯一ID
      unique = UUID.randomUUID().toString();
    }
  }

  private void executeEnd() {
    // 如果当前上下文为空.
    if (context == null) {
      // 设置当前系统时间为0,代表线程执行完毕.
      execStart = 0;
      // 设置线程结束运行时的唯一ID
      unique = null;
    }
  }

  /**
   * 给线程设置一个上下文环境对象.
   *
   * <p>代表线程正在运行着.
   *
   * @param contextParam 上下文对象.
   * @author admin
   */
  public void beginEmission(final StudyContext contextParam) {
    // 设置执行开始时间.
    executeStart();
    // 设置上下文.
    this.context = contextParam;
  }

  /**
   * 将线程上下文环境对象设置为空.
   *
   * <p>代表线程运行完毕.
   *
   * @author admin
   */
  public void endEmission() {
    // 设置当前上下文为空.
    context = null;
    // 设置执行结束时间.
    executeEnd();
  }

  /**
   * 当线程开始时,开始时间设置成当前系统的时间戳毫秒数.
   *
   * @param unique .
   * @author admin
   */
  private void executeStartV2(final String unique) {
    // 如果当前上下文为空.
    if (context == null) {
      // 设置当前系统时间为开始时间,代表线程开始执行.
      execStart = System.currentTimeMillis();
      // 设置线程开始运行时的唯一ID
      this.unique = unique;
    }
  }

  private void executeEndV2() {
    // 如果当前上下文为空.
    if (context == null) {
      // 设置当前系统时间为0,代表线程执行完毕.
      execStart = 0;
      // 设置线程结束运行时的唯一ID
      unique = null;
    }
  }

  /**
   * 给线程设置一个上下文环境对象.
   *
   * <p>代表线程正在运行着.
   *
   * @param unique .
   * @param contextParam 上下文对象.
   * @author admin
   */
  public void beginEmissionV2(final String unique, final StudyContext contextParam) {
    // 设置执行开始时间.
    executeStartV2(unique);
    // 设置上下文.
    this.context = contextParam;
  }

  /**
   * 将线程上下文环境对象设置为空.
   *
   * <p>代表线程运行完毕.
   *
   * @author admin
   */
  public void endEmissionV2() {
    // 设置当前上下文为空.
    context = null;
    // 设置执行结束时间.
    executeEndV2();
  }
}
