package org.study.juli.logging.api.metainfo;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class LogRecord {
  /** . */
  private final Map<String, String> customs = new LinkedHashMap<>(Constants.MAP_CAPACITY);
  /** . */
  private Level level;
  /** . */
  private String sourceClassName;
  /** . */
  private String sourceMethodName;
  /** . */
  private String message;
  /** . */
  private int threadId;
  /** . */
  private String threadName;
  /** . */
  private Throwable thrown;
  /** . */
  private String loggerName;
  /** . */
  private Instant instant;
  /** . */
  private Object[] parameters = new Object[0];
  /** . */
  private String uniqueId;
  /** . */
  private long serialNumber;
  /** . */
  private int lineNumber;
  /** . */
  private String host;
  /** . */
  private String port;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @param msg .
   * @author admin
   */
  public LogRecord(final Level level, final String msg) {
    this.level = level;
    this.message = msg;
    this.instant = Instant.now();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return int .
   * @author admin
   */
  public int getThreadId() {
    return threadId;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param threadId .
   * @author admin
   */
  public void setThreadId(final int threadId) {
    this.threadId = threadId;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getThreadName() {
    return threadName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param threadName .
   * @author admin
   */
  public void setThreadName(final String threadName) {
    this.threadName = threadName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param uniqueId .
   * @author admin
   */
  public void setUniqueId(final String uniqueId) {
    this.uniqueId = uniqueId;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getLoggerName() {
    return loggerName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @author admin
   */
  public void setLoggerName(final String name) {
    loggerName = name;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getSourceClassName() {
    return sourceClassName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param sourceClassName .
   * @author admin
   */
  public void setSourceClassName(final String sourceClassName) {
    this.sourceClassName = sourceClassName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getSourceMethodName() {
    return sourceMethodName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param sourceMethodName .
   * @author admin
   */
  public void setSourceMethodName(final String sourceMethodName) {
    this.sourceMethodName = sourceMethodName;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getMessage() {
    return message;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message .
   * @author admin
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Object .
   * @author admin
   */
  public Object[] getParameters() {
    return parameters.clone();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param parameters .
   * @author admin
   */
  public void setParameters(final Object[] parameters) {
    this.parameters = parameters.clone();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Instant .
   * @author admin
   */
  public Instant getInstant() {
    return instant;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param instant .
   * @author admin
   */
  public void setInstant(final Instant instant) {
    this.instant = instant;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Throwable .
   * @author admin
   */
  public Throwable getThrown() {
    return thrown;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param thrown .
   * @author admin
   */
  public void setThrown(final Throwable thrown) {
    this.thrown = thrown;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Level .
   * @author admin
   */
  public Level getLevel() {
    return level;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param level .
   * @author admin
   */
  public void setLevel(final Level level) {
    this.level = level;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return long .
   * @author admin
   */
  public long getSerialNumber() {
    return serialNumber;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param serialNumber .
   * @author admin
   */
  public void setSerialNumber(final long serialNumber) {
    this.serialNumber = serialNumber;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Map .
   * @author admin
   */
  public Map<String, String> getCustoms() {
    return customs;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param key .
   * @param value .
   * @author admin
   */
  public void setCustom(final String key, final String value) {
    customs.put(key, value);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return int .
   * @author admin
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param lineNumber .
   * @author admin
   */
  public void setLineNumber(final int lineNumber) {
    this.lineNumber = lineNumber;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getHost() {
    return host;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param host .
   * @author admin
   */
  public void setHost(final String host) {
    this.host = host;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return String .
   * @author admin
   */
  public String getPort() {
    return port;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param port .
   * @author admin
   */
  public void setPort(final String port) {
    this.port = port;
  }
}
