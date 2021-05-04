package org.study.juli.logging.handler;

import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.formatter.Formatter;

/**
 * This is a class description.
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
   * @param logRecord .
   * @author admin
   */
  void publish(LogRecord logRecord);

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
   * @throws SecurityException .
   * @author admin
   */
  void close() throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param newFormatter .
   * @author admin
   */
  void setFormatter(Formatter newFormatter);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param encoding .
   * @throws SecurityException .
   * @author admin
   */
  void setEncoding(String encoding) throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param newFilter .
   * @throws SecurityException .
   * @author admin
   */
  void setFilter(Filter newFilter) throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param newLevel .
   * @throws SecurityException .
   * @author admin
   */
  void setLevel(Level newLevel) throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param logRecord .
   * @return b.
   * @author admin
   */
  boolean isLoggable(LogRecord logRecord);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  void checkPermission() throws SecurityException;
}
