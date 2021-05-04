package org.study.juli.logging.filter;

import java.util.function.BooleanSupplier;
import org.study.juli.logging.core.LogRecord;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Filter extends BooleanSupplier {

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
   * @return b.
   * @author admin
   */
  @Override
  default boolean getAsBoolean() {
    return false;
  }
}
