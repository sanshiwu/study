package org.study.juli.logging.logger;

import java.util.Objects;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.manager.AbstractLogManager;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-01 19:47
 * @since 2021-04-01 19:47:00
 */
public class JuliLogger extends AbstractJuliLogger {

  public JuliLogger(String name, AbstractLogManager manager) {
    this.manager = manager;
    this.config = new ConfigurationData();
    this.name = name;
  }

  public JuliLogger(String name) {
    this.name = name;
    config = new ConfigurationData();
  }

  public void checkPermission() throws SecurityException {
    if (manager == null) {
      manager = AbstractLogManager.getLogManager();
    }
    manager.checkPermission();
  }

  public static JuliLogger getLogger(String name) {
    AbstractLogManager manager = AbstractLogManager.getLogManager();
    return manager.demandLogger(name);
  }

  public void setFilter(Filter newFilter) throws SecurityException {
    checkPermission();
    config.setFilter(newFilter);
  }

  public Filter getFilter() {
    return config.getFilter();
  }

  public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    doLog(lr);
  }

  public void logp(
      Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    Object[] params = {param1};
    lr.setParameters(params);
    lr.setUniqueId("-");
    doLog(lr);
  }

  public void logp(
      Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    lr.setParameters(params);
    lr.setUniqueId("-");
    doLog(lr);
  }

  public void logp(LogRecord lr) {
    doLog(lr);
  }

  public void logp(
      Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    lr.setThrown(thrown);
    doLog(lr);
  }



  public void setLevel(Level newLevel) throws SecurityException {
    checkPermission();
    synchronized (TREE_LOCK) {
      config.setLevelObject(newLevel);
      config.setLevelValue(newLevel.intValue());
    }
  }

  public final boolean isLevelInitialized() {
    return config.getLevelObject() != null;
  }

  public Level getLevel() {
    return config.getLevelObject();
  }



  public String getName() {
    return name;
  }

  public void addHandler(Handler handler) throws SecurityException {
    Objects.requireNonNull(handler);
    checkPermission();
    config.addHandler(handler);
  }

  public void removeHandler(Handler handler) throws SecurityException {
    checkPermission();
    if (handler == null) {
      return;
    }
    config.removeHandler(handler);
  }

  public Handler[] getHandlers() {
    return accessCheckedHandlers();
  }

  public Handler[] accessCheckedHandlers() {
    return config.getHandlers().toArray(EMPTY_HANDLERS);
  }

  public void setUseParentHandlers(boolean useParentHandlers) {
    checkPermission();
    config.setUseParentHandlers(useParentHandlers);
  }

  public boolean hasUseParentHandlers() {
    return config.isUseParentHandlers();
  }

  public JuliLogger getParent() {
    return parent;
  }

  public void setParent(JuliLogger parent) {
    if (manager == null) {
      manager = AbstractLogManager.getLogManager();
    }
    manager.checkPermission();
    doSetParent(parent);
  }

  private void doSetParent(JuliLogger newParent) {
    synchronized (TREE_LOCK) {
      parent = newParent;
    }
  }
}
