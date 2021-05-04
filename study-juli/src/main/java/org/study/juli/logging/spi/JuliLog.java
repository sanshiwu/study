package org.study.juli.logging.spi;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.study.juli.logging.core.Constants;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.formatter.StudyJuliMessageFormat;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.logger.JuliLogger;
import org.study.juli.logging.thread.StudyThread;
import org.study.juli.logging.utils.LogManagerUtils;

/**
 * Juli日志的核心API,提供所有日志级别的方法,由LogFactory动态创建.
 *
 * <p>每当使用LogFactory.getLog方法时都会创建一个Logging对象,并绑定到JDK Logger对象.
 *
 * @author admin
 */
public class JuliLog implements Log {
  /** 全局日志计数. */
  private static final AtomicLong GLOBAL_COUNTER = new AtomicLong(0L);
  /** 一个Logger对象对应一个Logging对象. */
  private final JuliLogger logger;
  /** 方法的当前堆栈元素,采用全局变量的原因是同一个对象,方法调用栈都是相同的,不用每次方法都调用一次(否则性能下降很多). */
  private StackTraceElement stackTraceElement;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public JuliLog() {
    logger = JuliLogger.getLogger(JuliLog.class.getName());
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @author admin
   */
  public JuliLog(final String name) {
    logger = JuliLogger.getLogger(name);
  }

  /**
   * 日志的核心方法.此方法必须经过logCore调用.
   *
   * <p>否则方法调用栈会出现异常.
   *
   * @param level 日志打印级别.
   * @param message 日志消息.
   * @param throwable 日志传递进来的异常.
   * @author admin
   */
  private void log(final Level level, final String message, final Throwable throwable) {
    LogRecord lr = new LogRecord(level, message);
    // 一个Logging实例初始化一次. 因为调用栈都是一样的,直接获取某个元素即可.
    if (stackTraceElement == null) {
      // 当前方法的异常.
      Throwable stackTrace = new Throwable();
      // 当前方法的调用栈.
      StackTraceElement[] stackTraceElements = stackTrace.getStackTrace();
      // 当前方法的调用栈深度是4. 因此获取第三个元素即可拿到调用者类.
      stackTraceElement = stackTraceElements[Constants.STACK_TRACE_ELEMENT];
    }
    // 设置异常栈.
    lr.setThrown(throwable);
    // 获取当前方法调用者的类全路径.
    String className = logger.getName();
    // 获取当前方法调用者的类方法.
    String classMethod = stackTraceElement.getMethodName();
    // 设置日志调用的源类路径和方法.
    lr.setSourceClassName(className);
    lr.setSourceMethodName(classMethod);
    // 设置行号.
    int lineNumber = stackTraceElement.getLineNumber();
    lr.setLineNumber(lineNumber);
    // 查看是否使用唯一序列号ID.
    String unique = LogManagerUtils.getProperty(Constants.UNIQUE, Constants.FALSE);
    if (unique.equals(Constants.TRUE)) {
      // 获取当前线程.
      Thread thread = Thread.currentThread();
      // 如果不是StudyThread,无法处理唯一日志消息ID.
      if (thread instanceof StudyThread) {
        StudyThread studyThread = (StudyThread) thread;
        // 为每条日志设置一个唯一的ID.
        lr.setUniqueId(studyThread.getUnique());
      }
    }
    // 为每条日志设置一个自增长的序列号.
    long globalCounter = GLOBAL_COUNTER.incrementAndGet();
    lr.setSerialNumber(globalCounter);
    // 当前进程的ip和port.
    lr.setHost(LogManagerUtils.getProperty(".host", null));
    lr.setPort(LogManagerUtils.getProperty(".port", null));
    logger.logp(lr);
  }

  /**
   * 所有日志级别对象的方法首先调用logCore方法.
   *
   * <p>这样的目的是保证方法调用栈的正确性,直接调用log方法会导致调用栈异常.
   *
   * @param level 日志打印级别.
   * @param message 日志消息.
   * @author admin
   */
  private void logCore(final Level level, final String message) {
    log(level, message, null);
  }

  /**
   * 日志的核心方法.
   *
   * <p>所有公共的方法,必须先调用logCore调用.
   *
   * @param level 日志打印级别.
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  private void logCore(final Level level, final String message, final Object[] args) {
    Objects.requireNonNull(message, "日志消息不能为空.");
    Objects.requireNonNull(args, "日志消息参数不能为空.");
    final int argsLen = args.length;
    // 没找到大括号,直接返回原消息.
    final int index = message.indexOf(Constants.BRACE);
    if (args.length == 0 || index == -1) {
      log(level, message, null);
    } else {
      // 检查最后一个参数是不是异常参数.
      final int lastArrIdx = argsLen - 1;
      final Object lastEntry = args[lastArrIdx];
      final Throwable throwable = lastEntry instanceof Throwable ? (Throwable) lastEntry : null;
      final String messageFormat = StudyJuliMessageFormat.format(message, args);
      log(level, messageFormat, throwable);
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handler .
   * @throws SecurityException .
   * @author admin
   */
  @Override
  public void addHandler(final Handler handler) throws SecurityException {
    Objects.requireNonNull(handler);
    logger.addHandler(handler);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param newLevel .
   * @throws SecurityException .
   * @author admin
   */
  @Override
  public void setLevel(final Level newLevel) throws SecurityException {
    logger.setLevel(newLevel);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void info(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.INFO, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void info(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void debug(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.FINE, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void debug(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void trace(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.FINER, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void trace(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void warn(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.WARNING, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void warn(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void error(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.SEVERE, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void error(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message);
  }

  /**
   * fatal日志消息,代表致命错误,优先级最高的日志级别.
   *
   * <p>.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void fatal(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.SEVERE, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void fatal(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void config(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.CONFIG, message);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void config(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.CONFIG, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void all(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.ALL, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void all(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.ALL, message, args);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  @Override
  public void off(final String message) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.

    logCore(Level.OFF, message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志传递进来的参数.
   * @author admin
   */
  @Override
  public void off(final String message, final Object... args) {
    // Logger的日志级别优先过滤不合法的日志,之后用处理器handler的日志级别过滤不合法的日志,最后才是Filter自定义过滤.
    logCore(Level.OFF, message, args);
  }
}
