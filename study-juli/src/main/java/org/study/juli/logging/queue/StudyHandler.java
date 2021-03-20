package org.study.juli.logging.queue;

import java.util.function.BooleanSupplier;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @param  <E> .
 * @author admin
 */
public interface StudyHandler<E> extends BooleanSupplier {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param event 任意对象.
   * @author admin
   */
  void handle(E event);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return boolean 函数式标准接口方法.
   * @author admin
   */
  @Override
  default boolean getAsBoolean() {
    return false;
  }
}
