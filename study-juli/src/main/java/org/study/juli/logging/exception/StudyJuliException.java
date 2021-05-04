package org.study.juli.logging.exception;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class StudyJuliException extends Exception {

  private static final long serialVersionUID = -1L;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message .
   * @author admin
   */
  public StudyJuliException(final String message) {
    super(message);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param message .
   * @param cause .
   * @author admin
   */
  public StudyJuliException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param cause .
   * @author admin
   */
  public StudyJuliException(final Throwable cause) {
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
   * @author admin
   */
  public StudyJuliException(
      final String message,
      final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
