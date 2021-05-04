package org.study.juli.logging.handler;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.study.juli.logging.context.WorkerContext;
import org.study.juli.logging.context.WorkerStudyContextImpl;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.formatter.Formatter;
import org.study.juli.logging.manager.AbstractLogManager;
import org.study.juli.logging.monitor.GuardianConsumerMonitor;
import org.study.juli.logging.monitor.Monitor;
import org.study.juli.logging.monitor.ThreadMonitor;
import org.study.juli.logging.pressure.policy.StudyRejectedPolicy;
import org.study.juli.logging.queue.StudyHandler;
import org.study.juli.logging.thread.StudyThreadFactory;
import org.study.juli.logging.worker.ProducerNoticeConsumerWorker;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractHandler implements Handler {
  /** 线程阻塞的最大时间时10秒.如果不超过15秒,打印warn.如果超过15秒打印异常堆栈. */
  protected static final Monitor CHECKER = new ThreadMonitor(15000L);
  /** 线程池. */
  protected static final GuardianConsumerMonitor GUARDIAN = new GuardianConsumerMonitor();
  /** 线程池. */
  protected static final ExecutorService LOG_PRODUCER =
      new ThreadPoolExecutor(
          1,
          1,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(5000),
          new StudyThreadFactory("log-producer", CHECKER),
          new StudyRejectedPolicy());

  /** 线程池. */
  protected static final ExecutorService LOG_GUARDIAN_CONSUMER =
      new ThreadPoolExecutor(
          1,
          1,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(5000),
          new StudyThreadFactory("log-guardian-consumer", CHECKER),
          new StudyRejectedPolicy());

  /** 线程池. CallerRunsPolicy 拒绝策略不丢数据,因为在主线程上执行. */
  protected static final ExecutorService LOG_CONSUMER =
      new ThreadPoolExecutor(
          1,
          1,
          0,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(5000),
          new StudyThreadFactory("log-consumer", CHECKER),
          new StudyRejectedPolicy());
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
          new LinkedBlockingQueue<>(5000),
          new StudyThreadFactory("log-producer-notice-consumer", CHECKER),
          new StudyRejectedPolicy());
  /** 工作任务上下文. */
  protected static final WorkerContext LOG_PRODUCER_NOTICE_CONSUMER_CONTEXT =
      new WorkerStudyContextImpl(LOG_PRODUCER_NOTICE_CONSUMER, SCHEDULED_EXECUTOR_SERVICE);
  /** . */
  protected static final int OFF_VALUE = Level.OFF.intValue();
  /** 全局handler日志计数. */
  protected static final AtomicLong GLOBAL_COUNTER = new AtomicLong(0L);

  static {
    // 线程监控任务.
    CHECKER.monitor(LOG_PRODUCER_CONTEXT);
    // 线程监控任务.
    CHECKER.monitor(LOG_CONSUMER_CONTEXT);
    // 守护消费监控任务.
    GUARDIAN.monitor(LOG_GUARDIAN_CONSUMER_CONTEXT);
  }

  /** 单个handler日志计数. */
  protected final AtomicLong counter = new AtomicLong(0L);
  /** 生产通知消费处理器.为Handler自己的队列创建一个生产者通知消费者处理程序. */
  protected final StudyHandler<Handler> producerNoticeConsumerWorker =
      new ProducerNoticeConsumerWorker();
  /** 一个非公平锁,fair=false. */
  protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
  /** 非公平读锁. */
  protected final Lock readLock = this.readWriteLock.readLock();
  /** 非公平写锁. */
  protected final Lock writeLock = this.readWriteLock.writeLock();
  /** . */
  protected final AbstractLogManager manager = AbstractLogManager.getLogManager();
  /** 代表当前处理器接收到最后一条日志的时间,0L表示从来没接收到. */
  protected long sys;
  /** 按照文件名翻转日志文件. */
  protected long initialization;
  /** 间隔. */
  protected int interval;
  /** 间隔格式化. */
  protected DateTimeFormatter intervalFormatter;
  /** . */
  protected boolean rotatable = true;
  /** . */
  protected Filter filter;
  /** . */
  protected Formatter formatter;
  /** . */
  protected Level logLevel = Level.ALL;
  /** . */
  protected String encoding;

  /** 生产日志处理器. */
  protected StudyHandler<LogRecord> producerWorker;

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
    String value = AbstractLogManager.getLogManager().getProperty(className + name);
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

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Formatter
   * @author admin
   */
  public synchronized Formatter getFormatter() {
    return formatter;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param newFormatter .
   * @throws SecurityException .
   * @author admin
   */
  @Override
  public synchronized void setFormatter(final Formatter newFormatter) throws SecurityException {
    checkPermission();
    formatter = Objects.requireNonNull(newFormatter);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String
   * @author admin
   */
  public synchronized String getEncoding() {
    return encoding;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param encoding .
   * @author admin
   */
  @Override
  public synchronized void setEncoding(final String encoding) throws SecurityException {
    checkPermission();
    this.encoding = encoding;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Filter
   * @author admin
   */
  public synchronized Filter getFilter() {
    return filter;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  @Override
  public synchronized void setFilter(final Filter newFilter) throws SecurityException {
    checkPermission();
    filter = newFilter;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Level .
   * @author admin
   */
  public synchronized Level getLevel() {
    return logLevel;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  @Override
  public synchronized void setLevel(final Level newLevel) throws SecurityException {
    checkPermission();
    logLevel = newLevel;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public boolean isLoggable(final LogRecord logRecord) {
    final int levelValue = getLevel().intValue();
    if (logRecord.getLevel().intValue() < levelValue || levelValue == OFF_VALUE) {
      return false;
    }
    final Filter filterTemp = getFilter();
    if (filterTemp == null) {
      return true;
    }
    return filterTemp.isLoggable(logRecord);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  @Override
  public void checkPermission() throws SecurityException {
    manager.checkPermission();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return AtomicLong .
   * @author admin
   */
  public AtomicLong getCounter() {
    return counter;
  }
}
