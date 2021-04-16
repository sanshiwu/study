package org.study.juli.logging.queue;

import java.util.concurrent.LinkedBlockingDeque;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.logger.JuliLogger;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @param <T> 泛型对象.
 * @author admin
 */
public abstract class AbstractQueue<T> implements StudyQueue<T> {
  /** . */
  private static final JuliLogger LOGGER = JuliLogger.getLogger(AbstractQueue.class.getName());
  /** 阻塞队列名称,按照业务划分. */
  protected String target;
  /** 双端链表阻塞队列,可以头尾操作. */
  protected LinkedBlockingDeque<T> queue;
  /** 队列初始容量默认5000. */
  private int capacity = Constants.CAPACITY;

  /**
   * 创建一个容量Integer.MAX_VALUE的队列.
   *
   * <p>Another description after blank line.
   *
   * @param target 队列的名.
   * @author admin
   */
  protected AbstractQueue(final String target) {
    this.target = target;
    this.queue = new LinkedBlockingDeque<>(capacity);
    LOGGER.logp(Level.SEVERE, AbstractQueue.class.getName(), "构造方法", "创建队列{0}", target);
  }

  /**
   * 创建一个容量capacity的队列.
   *
   * <p>Another description after blank line.
   *
   * @param target 队列名.
   * @param capacity 队列容量.
   * @author admin
   */
  protected AbstractQueue(final int capacity, final String target) {
    this.target = target;
    this.capacity = capacity;
    this.queue = new LinkedBlockingDeque<>(capacity);
  }

  @Override
  public T poll() {
    return queue.poll();
  }
}
