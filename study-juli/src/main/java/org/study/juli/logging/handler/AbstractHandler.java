package org.study.juli.logging.handler;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import org.study.juli.logging.context.WorkerContext;
import org.study.juli.logging.context.WorkerStudyContextImpl;
import org.study.juli.logging.monitor.GuardianConsumerMonitor;
import org.study.juli.logging.monitor.Monitor;
import org.study.juli.logging.monitor.ThreadMonitor;
import org.study.juli.logging.thread.StudyThreadFactory;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractHandler extends Handler {
  /** 线程阻塞的最大时间时10秒.如果不超过15秒,打印warn.如果超过15秒打印异常堆栈. */
  protected static final Monitor CHECKER = new ThreadMonitor(15000L);
  /** 线程池. */
  protected static final GuardianConsumerMonitor GUARDIAN = new GuardianConsumerMonitor();
  /** 线程池. */
  protected static final ExecutorService LOG_PRODUCER =
      new ThreadPoolExecutor(
          2,
          2,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(1000000),
          new StudyThreadFactory("log-producer", CHECKER),
          new ThreadPoolExecutor.AbortPolicy());

  /** 线程池. */
  protected static final ExecutorService LOG_GUARDIAN_CONSUMER =
      new ThreadPoolExecutor(
          1,
          1,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(1000000),
          new StudyThreadFactory("log-guardian-consumer", CHECKER),
          new ThreadPoolExecutor.AbortPolicy());

  /** 线程池. CallerRunsPolicy 拒绝策略不丢数据,因为在主线程上执行. */
  protected static final ExecutorService LOG_CONSUMER =
      new ThreadPoolExecutor(
          2,
          2,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(1000000),
          new StudyThreadFactory("log-consumer", CHECKER),
          new ThreadPoolExecutor.AbortPolicy());
  /** 服务器端的定时调度线程池. */
  protected static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
      new ScheduledThreadPoolExecutor(3, new StudyThreadFactory("study_scheduled", null));
  /** 工作任务上下文. */
  protected static final WorkerContext LOG_PRODUCER_CONTEXT =
      new WorkerStudyContextImpl(LOG_PRODUCER, SCHEDULED_EXECUTOR_SERVICE);
  /** 工作任务上下文. */
  protected static final WorkerContext LOG_CONSUMER_CONTEXT =
      new WorkerStudyContextImpl(LOG_CONSUMER, SCHEDULED_EXECUTOR_SERVICE);
  /** 工作任务上下文. */
  protected static final WorkerContext LOG_GUARDIAN_CONSUMER_CONTEXT =
      new WorkerStudyContextImpl(LOG_GUARDIAN_CONSUMER, SCHEDULED_EXECUTOR_SERVICE);
  /** 线程池. */
  protected static final ExecutorService LOG_PRODUCER_NOTICE_CONSUMER =
      new ThreadPoolExecutor(
          1,
          1,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(1000000),
          new StudyThreadFactory("log-producer-notice-consumer", CHECKER),
          new ThreadPoolExecutor.AbortPolicy());
  /** 工作任务上下文. */
  protected static final WorkerContext LOG_PRODUCER_NOTICE_CONSUMER_CONTEXT =
      new WorkerStudyContextImpl(LOG_PRODUCER_NOTICE_CONSUMER, SCHEDULED_EXECUTOR_SERVICE);

  static {
    // 线程监控任务.
    CHECKER.monitor(LOG_PRODUCER_CONTEXT);
    // 线程监控任务.
    CHECKER.monitor(LOG_CONSUMER_CONTEXT);
    // 守护消费监控任务.
    GUARDIAN.monitor(LOG_GUARDIAN_CONSUMER_CONTEXT);
  }

  /** 代表当前处理器接收到最后一条日志的时间,0L表示从来没接收到. */
  protected long sys;
  /** . */
  protected long initialization;
  /** 间隔. */
  protected int interval;
  /** 间隔格式化. */
  protected DateTimeFormatter intervalFormatter;
  /** . */
  protected boolean rotatable = true;
  /** 一个非公平锁,fair=false. */
  protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
  /** 非公平读锁. */
  protected final Lock readLock = this.readWriteLock.readLock();
  /** 非公平写锁. */
  protected final Lock writeLock = this.readWriteLock.writeLock();

  /**
   * 关闭资源方法,一般处理优雅关闭应用程序时调用.
   *
   * <p>如果强行kill -9 方法是不会调用的.
   *
   * @throws SecurityException 抛出安全异常.
   * @author admin
   */
  @Override
  public void close() throws SecurityException {
    // 每个子类也要关闭响应的资源,暂时未处理.
    LOG_CONSUMER.shutdown();
    LOG_PRODUCER.shutdown();
  }

  /**
   * 用Key从JDL LogManager读取一个配置的value.
   *
   * <p>Another description after blank line.
   *
   * @param name 属性名.
   * @param defaultValue 默认属性名.
   * @return 返回属性名对应的值.
   * @author admin
   */
  public String getProperty(final String name, final String defaultValue) {
    // 获取当前的类的全路径.
    String className = this.getClass().getName();
    // 获取当前类的配置属性.
    String value = LogManager.getLogManager().getProperty(className + name);
    // 如果空,使用默认值.
    if (value == null) {
      value = defaultValue;
    } else {
      // 如果不为空,去掉空格.
      value = value.trim();
    }
    return value;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return 返回处理器最后接收到日志的时间.
   * @author admin
   */
  public long getSys() {
    return this.sys;
  }
}
