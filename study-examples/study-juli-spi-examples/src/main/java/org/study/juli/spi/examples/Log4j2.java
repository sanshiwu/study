package org.study.juli.spi.examples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.study.juli.logging.base.Log;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class Log4j2 implements Log {

  /** . */
  private final Logger logger;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public Log4j2() {
    this.logger = LogManager.getLogger(Log4j2.class.getName());
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @author admin
   */
  public Log4j2(final String name) {
    this.logger = LogManager.getLogger(name);
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
    //
    logger.info(message, args);
  }

  @Override
  public void info(String message) {
    logger.info(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void debug(final String message, final Object... args) {
    //
    logger.debug(message, args);
  }

  @Override
  public void debug(String message) {
    logger.debug(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void trace(final String message, final Object... args) {
    //
    logger.trace(message, args);
  }

  @Override
  public void trace(String message) {
    logger.trace(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void warn(final String message, final Object... args) {
    //
    logger.warn(message, args);
  }

  @Override
  public void warn(String message) {
    logger.warn(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void error(final String message, final Object... args) {
    //
    logger.error(message, args);
  }

  @Override
  public void error(String message) {
    logger.error(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void fatal(final String message, final Object... args) {
    //
    logger.fatal(message, args);
  }

  @Override
  public void fatal(String message) {
    logger.fatal(message);
  }
}
