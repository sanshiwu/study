package org.study.juli.logging.core.manager;


import java.util.List;
import java.util.Map;
import org.study.juli.logging.api.filter.Filter;
import org.study.juli.logging.api.handler.Handler;
import org.study.juli.logging.api.logger.Logger;
import org.study.juli.logging.api.metainfo.Level;
import org.study.juli.logging.api.metainfo.LogRecord;
import org.study.juli.logging.core.logger.ConfigurationData;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractJuliLogger implements Logger {
  /** . */
  protected static final int OFF_VALUE = Level.OFF.intValue();
  /** . */
  protected static final Handler[] EMPTY_HANDLERS = new Handler[0];
  /** . */
  protected static final Object TREE_LOCK = new Object();
  /** . */
  protected ConfigurationData config;
  /** . */
  protected AbstractLogManager manager;
  /** . */
  protected String name;
  /** . */
  protected JuliLogger parent;

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param logRecord .
   * @author admin
   */
  public void log(final LogRecord logRecord) {
    if (!isLoggable(logRecord.getLevel())) {
      return;
    }
    Filter theFilter = config.getFilter();
    if (theFilter != null && !theFilter.isLoggable(logRecord)) {
      return;
    }
    List<Handler> handlers = config.getHandlers();
    if (handlers.isEmpty()) {
      Map<ClassLoader, ClassLoaderLogInfo> cll = manager.classLoaderLoggers;
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      ClassLoaderLogInfo classLoaderLogInfo = cll.get(classLoader);
      Logger rootLogger = classLoaderLogInfo.rootLogger;
      Handler[] handlers2 = rootLogger.getHandlers();
      for (Handler handler : handlers2) {
        handler.publish(logRecord);
      }
    } else {
      for (Handler handler : handlers) {
        handler.publish(logRecord);
      }
    }
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param msg .
   * @author admin
   */
  public void log(final Level level, final String msg) {
    if (!isLoggable(level)) {
      return;
    }
    final LogRecord lr = new LogRecord(level, msg);
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param msg .
   * @param param1 .
   * @author admin
   */
  public void log(final Level level, final String msg, final Object param1) {
    if (!isLoggable(level)) {
      return;
    }
    final LogRecord lr = new LogRecord(level, msg);
    final Object[] params = {param1};
    lr.setParameters(params);
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param msg .
   * @param params .
   * @author admin
   */
  public void log(final Level level, final String msg, final Object[] params) {
    if (!isLoggable(level)) {
      return;
    }
    final LogRecord lr = new LogRecord(level, msg);
    lr.setParameters(params);
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param msg .
   * @param thrown .
   * @author admin
   */
  public void log(final Level level, final String msg, final Throwable thrown) {
    if (!isLoggable(level)) {
      return;
    }
    final LogRecord lr = new LogRecord(level, msg);
    lr.setThrown(thrown);
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param lr .
   * @author admin
   */
  public void doLog(final LogRecord lr) {
    lr.setLoggerName(name);
    log(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @return boolean .
   * @author admin
   */
  public boolean isLoggable(final Level level) {
    int levelValue = config.getLevelValue();
    boolean flag = true;
    if (level.intValue() < levelValue || levelValue == OFF_VALUE) {
      flag = false;
    }
    return flag;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void severe(final String msg) {
    log(Level.SEVERE, msg);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void warning(final String msg) {
    log(Level.WARNING, msg);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void info(final String msg) {
    log(Level.INFO, msg);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void config(final String msg) {
    log(Level.CONFIG, msg);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void fine(final String msg) {
    log(Level.FINE, msg);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void finer(final String msg) {
    log(Level.FINER, msg);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param msg .
   * @author admin
   */
  public void finest(final String msg) {
    log(Level.FINEST, msg);
  }
}
