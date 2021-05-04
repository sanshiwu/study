package org.study.juli.logging.policy;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractPolicy implements Policy {

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return boolean .
   * @author admin
   */
  @Override
  public boolean isTriggeringEvent() {
    return false;
  }
}
