package org.study.juli.logging.policy;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-06 14:19
 * @since 2021-04-06 14:19:00
 */
public abstract class AbstractPolicy implements Policy {
  @Override
  public boolean isTriggeringEvent() {
    return false;
  }
}
