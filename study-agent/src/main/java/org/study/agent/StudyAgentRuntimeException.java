package org.study.agent;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class StudyAgentRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -1L;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   */
  public StudyAgentRuntimeException() {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message .
   */
  public StudyAgentRuntimeException(final String message) {
    super(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message .
   * @param cause .
   */
  public StudyAgentRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param cause .
   */
  public StudyAgentRuntimeException(final Throwable cause) {
    super(cause);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message .
   * @param cause .
   * @param enableSuppression .
   * @param writableStackTrace .
   */
  public StudyAgentRuntimeException(
      final String message,
      final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
