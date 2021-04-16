package org.study.juli.logging.policy;

import java.util.function.BooleanSupplier;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-06 14:19
 * @since 2021-04-06 14:19:00
 */
public interface Policy extends BooleanSupplier {

  boolean isTriggeringEvent();

  @Override
  boolean getAsBoolean();
}
