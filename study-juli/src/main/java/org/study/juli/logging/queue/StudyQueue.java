package org.study.juli.logging.queue;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @param <T> 泛型对象.
 * @author admin
 */
public interface StudyQueue<T> {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return 返回队列中元素的数量.
   * @author admin
   */
  int size();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return T .
   * @author admin
   */
  T poll();
}
