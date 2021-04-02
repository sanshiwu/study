package org.study.juli.logging.config;

import java.util.Map;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.logger.JuliLogger;
import org.study.juli.logging.manager.ClassLoaderLogInfo;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Management {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void createHandler(final String handlerClassName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void deleteHandler(final String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void updateHandler(final String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  JuliLogger getLogger(final String loggerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void closeHandler(final String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  Handler getHandler(final String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  Map<String, JuliLogger> loggers();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  Map<String, Handler> handlers();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  ClassLoaderLogInfo getClassLoaderLogInfo();
}
