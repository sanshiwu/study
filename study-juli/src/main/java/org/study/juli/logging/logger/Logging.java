package org.study.juli.logging.logger;

import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.base.Log;
import org.study.juli.logging.formatter.StudyJuliMessageHandler;

/**
 * Juli日志的核心API,提供所有日志级别的方法,由LogFactory动态创建.
 *
 * <p>每当使用LogFactory.getLog方法时都会创建一个Logging对象,并绑定到JDK Logger对象.
 *
 * @author admin
 */
@SuppressWarnings({"java:S1450"})
public class Logging implements Log {
  /** 一个Logger对象对应一个Logging对象. */
  private final Logger logger;
  /** 方法堆栈异常. */
  private Throwable stackTrace;
  /** 方法调用栈(java:S1450). */
  private StackTraceElement[] stackTraceElements;
  /** 方法的当前堆栈元素,采用全局变量的原因是同一个对象,都是相同的,不用每次方法都调用一次(否则性能下降很多). */
  private StackTraceElement stackTraceElement;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public Logging() {
    logger = Logger.getLogger(Logging.class.getName());
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public Logging(final String name) {
    logger = Logger.getLogger(name);
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
    // 一个Logging实例初始化一次. 因为调用栈都是一样的,直接获取某个元素即可.
    if (stackTrace == null) {
      // 当前方法的异常.
      stackTrace = new Throwable();
      // 当前方法的调用栈.
      stackTraceElements = this.stackTrace.getStackTrace();
      // 当前方法的调用栈深度是4. 因此获取第三个元素即可拿到调用者类.
      stackTraceElement = this.stackTraceElements[Constants.STACK_TRACE_ELEMENT];
    }
    // 获取当前方法调用者的类全路径.
    String className = logger.getName();
    // 获取当前方法调用者的类方法.
    String classMethod = stackTraceElement.getMethodName();
    // 调用JDK日志打印方法,循环查找当前logger注册的handler.循环调用handler的publish方法.
    logger.logp(level, className, classMethod, message, throwable);
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
      final String messageFormat = StudyJuliMessageHandler.format(message, args);
      log(level, messageFormat, throwable);
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void addHandler(Handler handler) throws SecurityException {
    Objects.requireNonNull(handler);
    logger.addHandler(handler);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void setLevel(Level newLevel) throws SecurityException {
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
    if (logger.isLoggable(Level.INFO)) {
      logCore(Level.INFO, message, args);
    }
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
    if (logger.isLoggable(Level.INFO)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.FINE)) {
      logCore(Level.FINE, message, args);
    }
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
    if (logger.isLoggable(Level.FINE)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.FINER)) {
      logCore(Level.FINER, message, args);
    }
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
    if (logger.isLoggable(Level.FINER)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.WARNING)) {
      logCore(Level.WARNING, message, args);
    }
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
    if (logger.isLoggable(Level.WARNING)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.SEVERE)) {
      logCore(Level.SEVERE, message, args);
    }
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
    if (logger.isLoggable(Level.SEVERE)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.SEVERE)) {
      logCore(Level.SEVERE, message, args);
    }
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
    if (logger.isLoggable(Level.CONFIG)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.CONFIG)) {
      logCore(Level.CONFIG, message);
    }
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
    if (logger.isLoggable(Level.CONFIG)) {
      logCore(Level.CONFIG, message, args);
    }
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
    if (logger.isLoggable(Level.ALL)) {
      logCore(Level.ALL, message);
    }
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
    if (logger.isLoggable(Level.ALL)) {
      logCore(Level.ALL, message, args);
    }
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
    if (logger.isLoggable(Level.OFF)) {
      logCore(Level.OFF, message);
    }
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
    if (logger.isLoggable(Level.OFF)) {
      logCore(Level.OFF, message, args);
    }
  }
}
