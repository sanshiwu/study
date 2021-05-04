package org.study.juli.logging.formatter;

import java.util.function.BooleanSupplier;
import org.study.juli.logging.core.LogRecord;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Formatter extends BooleanSupplier {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param logRecord .
   * @return s.
   * @author admin
   */
  String format(LogRecord logRecord);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return s.
   * @author admin
   */
  @Override
  default boolean getAsBoolean() {
    return false;
  }
}
