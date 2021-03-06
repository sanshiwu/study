package org.study.juli.logging.exception;

/**
 * Study Juli日志配置运行时异常.
 *
 * <p>日志配置运行时异常.
 *
 * @author admin
 */
public class StudyJuliConfigException extends RuntimeException {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 消息.
   * @author admin
   */
  public StudyJuliConfigException(final String message) {
    super(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param cause 异常.
   * @author admin
   */
  public StudyJuliConfigException(final Throwable cause) {
    super(cause);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message 消息.
   * @param cause   异常.
   * @author admin
   */
  public StudyJuliConfigException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
