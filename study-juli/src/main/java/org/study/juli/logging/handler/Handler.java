package org.study.juli.logging.handler;

import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.formatter.Formatter;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Handler {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void publish(LogRecord record);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void flush();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void close() throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void setFormatter(Formatter newFormatter);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void setEncoding(String encoding) throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void setFilter(Filter newFilter) throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void setLevel(Level newLevel) throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  boolean isLoggable(LogRecord record);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void checkPermission() throws SecurityException;
}
