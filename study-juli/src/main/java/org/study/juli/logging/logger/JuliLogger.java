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
 */
public class JuliLogger extends AbstractJuliLogger {

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @param manager .
   * @author admin
   */
  public JuliLogger(final String name, final AbstractLogManager manager) {
    this.manager = manager;
    this.config = new ConfigurationData();
    this.name = name;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @author admin
   */
  public JuliLogger(final String name) {
    this.name = name;
    config = new ConfigurationData();
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @return JuliLogger .
   * @author admin
   */
  public static JuliLogger getLogger(final String name) {
    AbstractLogManager manager = AbstractLogManager.getLogManager();
    return manager.demandLogger(name);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void checkPermission() throws SecurityException {
    if (manager == null) {
      manager = AbstractLogManager.getLogManager();
    }
    manager.checkPermission();
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return Filter .
   * @author admin
   */
  public Filter getFilter() {
    return config.getFilter();
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param newFilter .
   * @throws SecurityException .
   * @author admin
   */
  public void setFilter(final Filter newFilter) throws SecurityException {
    checkPermission();
    config.setFilter(newFilter);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param sourceClass .
   * @param sourceMethod .
   * @param msg .
   * @author admin
   */
  public void logp(
      final Level level, final String sourceClass, final String sourceMethod, final String msg) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param sourceClass .
   * @param sourceMethod .
   * @param msg .
   * @param param1 .
   * @author admin
   */
  public void logp(
      final Level level,
      final String sourceClass,
      final String sourceMethod,
      final String msg,
      final Object param1) {
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

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param sourceClass .
   * @param sourceMethod .
   * @param msg .
   * @param params .
   * @author admin
   */
  public void logp(
      final Level level,
      final String sourceClass,
      final String sourceMethod,
      final String msg,
      final Object[] params) {
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

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param lr .
   * @author admin
   */
  public void logp(final LogRecord lr) {
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param sourceClass .
   * @param sourceMethod .
   * @param msg .
   * @param thrown .
   * @author admin
   */
  public void logp(
      final Level level,
      final String sourceClass,
      final String sourceMethod,
      final String msg,
      final Throwable thrown) {
    if (!isLoggable(level)) {
      return;
    }
    LogRecord lr = new LogRecord(level, msg);
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    lr.setThrown(thrown);
    doLog(lr);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return boolean .
   * @author admin
   */
  public final boolean isLevelInitialized() {
    return config.getLevelObject() != null;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return Level .
   * @author admin
   */
  public Level getLevel() {
    return config.getLevelObject();
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param newLevel .
   * @author admin
   */
  public void setLevel(final Level newLevel) throws SecurityException {
    checkPermission();
    synchronized (TREE_LOCK) {
      config.setLevelObject(newLevel);
      config.setLevelValue(newLevel.intValue());
    }
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getName() {
    return name;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param handler .
   * @author admin
   */
  public void addHandler(final Handler handler) throws SecurityException {
    Objects.requireNonNull(handler);
    checkPermission();
    config.addHandler(handler);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param handler .
   * @author admin
   */
  public void removeHandler(final Handler handler) throws SecurityException {
    checkPermission();
    if (handler == null) {
      return;
    }
    config.removeHandler(handler);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return Handler .
   * @author admin
   */
  public Handler[] getHandlers() {
    return accessCheckedHandlers();
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return Handler .
   * @author admin
   */
  public Handler[] accessCheckedHandlers() {
    return config.getHandlers().toArray(EMPTY_HANDLERS);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param useParentHandlers .
   * @author admin
   */
  public void setUseParentHandlers(final boolean useParentHandlers) {
    checkPermission();
    config.setUseParentHandlers(useParentHandlers);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return boolean .
   * @author admin
   */
  public boolean hasUseParentHandlers() {
    return config.isUseParentHandlers();
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return JuliLogger .
   * @author admin
   */
  public JuliLogger getParent() {
    return parent;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param parent .
   * @author admin
   */
  public void setParent(final JuliLogger parent) {
    if (manager == null) {
      manager = AbstractLogManager.getLogManager();
    }
    manager.checkPermission();
    doSetParent(parent);
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param newParent .
   * @author admin
   */
  private void doSetParent(final JuliLogger newParent) {
    synchronized (TREE_LOCK) {
      parent = newParent;
    }
  }
}
