package org.study.juli.logging.core;

import java.time.Instant;

public class LogRecord {
  private Level level;
  private String sourceClassName;
  private String sourceMethodName;
  private String message;
  private int threadId;
  private String threadName;
  private Throwable thrown;
  private String loggerName;
  private Instant instant;
  private Object[] parameters = new Object[0];
  private String uniqueId;

  public LogRecord(Level level, String msg) {
    this.level = level;
    this.message = msg;
    this.instant = Instant.now();
  }

  public int getThreadId() {
    return threadId;
  }

  public void setThreadId(int threadId) {
    this.threadId = threadId;
  }

  public String getThreadName() {
    return threadName;
  }

  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }

  public void setInstant(Instant instant) {
    this.instant = instant;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getLoggerName() {
    return loggerName;
  }

  public void setLoggerName(String name) {
    loggerName = name;
  }

  public String getSourceClassName() {
    return sourceClassName;
  }

  public void setSourceClassName(String sourceClassName) {
    this.sourceClassName = sourceClassName;
  }

  public String getSourceMethodName() {
    return sourceMethodName;
  }

  public void setSourceMethodName(String sourceMethodName) {
    this.sourceMethodName = sourceMethodName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object[] getParameters() {
    return parameters.clone();
  }

  public void setParameters(Object[] parameters) {
    this.parameters = parameters.clone();
  }

  public Instant getInstant() {
    return instant;
  }

  public Throwable getThrown() {
    return thrown;
  }

  public void setThrown(Throwable thrown) {
    this.thrown = thrown;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }
}