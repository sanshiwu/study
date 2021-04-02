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
   * @author admin
   */
  boolean isLoggable(LogRecord record);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  default boolean getAsBoolean() {
    return false;
  }
}
