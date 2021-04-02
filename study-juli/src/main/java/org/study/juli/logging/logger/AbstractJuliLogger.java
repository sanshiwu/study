package org.study.juli.logging.logger;

import java.util.List;
import java.util.Map;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.manager.AbstractLogManager;
import org.study.juli.logging.manager.ClassLoaderLogInfo;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-02 15:20
 * @since 2021-04-02 15:20:00
 */
public abstract class AbstractJuliLogger implements Logger {
  protected static final int OFF_VALUE = Level.OFF.intValue();
  protected static final Handler[] emptyHandlers = new Handler[0];
  protected static final Object treeLock = new Object();
  protected ConfigurationData config;
  protected AbstractLogManager manager;
  protected String name;
  protected JuliLogger parent;

  public void log(LogRecord record) {
    if (!isLoggable(record.getLevel())) {
      return;
    }
    Filter theFilter = config.getFilter();
    if (theFilter != null && !theFilter.isLoggable(record)) {
      return;
    }
    List<Handler> handlers = config.getHandlers();
    if (handlers.isEmpty()) {
      Map<ClassLoader, ClassLoaderLogInfo> cll = manager.classLoaderLoggers;
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      ClassLoaderLogInfo classLoaderLogInfo = cll.get(classLoader);
      RootLogger rootLogger = classLoaderLogInfo.rootLogger;
      Handler[] handlers2 = rootLogger.getHandlers();
      for (Handler handler : handlers2) {
        handler.publish(record);
      }
    } else {
      for (Handler handler : handlers) {
        handler.publish(record);
      }
    }
  }

  public void doLog(LogRecord lr) {
    lr.setLoggerName(name);
    log(lr);
  }

  public void log(Level level, String msg) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    doLog(lr);
  }

  public void log(Level level, String msg, Object param1) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    Object[] params = {param1};
    lr.setParameters(params);
    doLog(lr);
  }

  public void log(Level level, String msg, Object[] params) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setParameters(params);
    doLog(lr);
  }

  public void log(Level level, String msg, Throwable thrown) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setThrown(thrown);
    doLog(lr);
  }

  public boolean isLoggable(Level level) {
    int levelValue = config.getLevelValue();
    boolean flag = true;
    if (level.intValue() < levelValue || levelValue == OFF_VALUE) {
      flag = false;
    }
    return flag;
  }

  public void severe(String msg) {
    log(Level.SEVERE, msg);
  }

  public void warning(String msg) {
    log(Level.WARNING, msg);
  }

  public void info(String msg) {
    log(Level.INFO, msg);
  }

  public void config(String msg) {
    log(Level.CONFIG, msg);
  }

  public void fine(String msg) {
    log(Level.FINE, msg);
  }

  public void finer(String msg) {
    log(Level.FINER, msg);
  }

  public void finest(String msg) {
    log(Level.FINEST, msg);
  }
}
