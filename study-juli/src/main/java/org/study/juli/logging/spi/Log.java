package org.study.juli.logging.spi;

import org.study.juli.logging.core.Level;
import org.study.juli.logging.handler.Handler;

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
  default void setLevel(Level newLevel) throws SecurityException {
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
  void info(String message, Object... args);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void info(String message);

  /**
   * 详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void debug(String message, Object... args);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void debug(String message);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void trace(String message, Object... args);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void trace(String message);

  /**
   * 警告日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void warn(String message, Object... args);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void warn(String message);

  /**
   * 严重日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void error(String message, Object... args);

  /**
   * 较详细日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void error(String message);

  /**
   * 致命错误日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @param args 日志消息格式化填充对象.
   * @author admin
   */
  void fatal(String message, Object... args);

  /**
   * 致命错误日志.
   *
   * <p>Another description after blank line.
   *
   * @param message 日志消息.
   * @author admin
   */
  void fatal(String message);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param args 日志传递进来的参数.
   * @param message 日志消息.
   * @author admin
   */
  default void off(String message, Object... args) {
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
  default void off(String message) {
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
  default void all(String message, Object... args) {
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
  default void all(String message) {
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
  default void config(String message, Object... args) {
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
  default void config(String message) {
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
