package org.study.juli.logging.handler;

import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.formatter.Formatter;

public interface Handler {

  void publish(LogRecord record);

  void flush();

  void close() throws SecurityException;

  void setFormatter(Formatter newFormatter);

  void setEncoding(String encoding) throws SecurityException;

  void setFilter(Filter newFilter) throws SecurityException;

  void setLevel(Level newLevel) throws SecurityException;

  boolean isLoggable(LogRecord record);

  void checkPermission() throws SecurityException;
}
