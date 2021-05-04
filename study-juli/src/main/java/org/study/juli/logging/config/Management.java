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
   * @param handlerClassName .
   * @author admin
   */
  void createHandler(String handlerClassName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handlerName .
   * @author admin
   */
  void deleteHandler(String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handlerName .
   * @author admin
   */
  void updateHandler(String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param loggerName .
   * @return JuliLogger
   * @author admin
   */
  JuliLogger getLogger(String loggerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handlerName .
   * @author admin
   */
  void closeHandler(String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handlerName .
   * @return Handler
   * @author admin
   */
  Handler getHandler(String handlerName);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Map
   * @author admin
   */
  Map<String, JuliLogger> loggers();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Map
   * @author admin
   */
  Map<String, Handler> handlers();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return ClassLoaderLogInfo
   * @author admin
   */
  ClassLoaderLogInfo getClassLoaderLogInfo();
}
