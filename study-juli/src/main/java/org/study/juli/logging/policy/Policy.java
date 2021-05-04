package org.study.juli.logging.policy;

import java.util.function.BooleanSupplier;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Policy extends BooleanSupplier {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return b.
   * @author admin
   */
  boolean isTriggeringEvent();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return b.
   * @author admin
   */
  @Override
  boolean getAsBoolean();
}
