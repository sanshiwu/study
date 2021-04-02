package org.study.juli.logging.spi;

import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.core.Level;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Log {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handler 添加handler.
   * @author admin
   */
  default void addHandler(Handler handler) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param newLevel 设置日志级别.
   * @throws SecurityException 抛出安全异常.
   * @author admin
   */
  default void setLevel(final Level newLevel) throws SecurityException {
    //
  }

  /**
   * 信息日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void info(final String message, final Object... args);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void info(final String message);

  /**
   * 详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void debug(final String message, final Object... args);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void debug(final String message);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void trace(final String message, final Object... args);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void trace(final String message);

  /**
   * 警告日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void warn(final String message, final Object... args);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void warn(final String message);

  /**
   * 严重日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void error(final String message, final Object... args);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void error(final String message);

  /**
   * 致命错误日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void fatal(final String message, final Object... args);

  /**
   * 致命错误日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void fatal(final String message);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param args 日志传递进来的参数.
   * @param message 日志消息.
   * @author admin
   */
  default void off(final String message, final Object... args) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  default void off(final String message) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param args 日志传递进来的参数.
   * @param message 日志消息.
   * @author admin
   */
  default void all(final String message, final Object... args) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  default void all(final String message) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param args 日志传递进来的参数.
   * @param message 日志消息.
   * @author admin
   */
  default void config(final String message, final Object... args) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  default void config(final String message) {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return 是否开启调试.
   * @author admin
   */
  default boolean isDebugEnabled() {
    return false;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return 是否开启Info.
   * @author admin
   */
  default boolean isInfoEnabled() {
    return false;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return 是否开启Warn.
   * @author admin
   */
  default boolean isWarnEnabled() {
    return false;
  }
}
