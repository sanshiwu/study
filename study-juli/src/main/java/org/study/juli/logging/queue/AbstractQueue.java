package org.study.juli.logging.queue;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.study.juli.logging.base.Constants;

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
  private static final Logger LOGGER = Logger.getLogger(AbstractQueue.class.getName());
  /** 阻塞队列名称,按照业务划分. */
  protected String target;
  /** 双端链表阻塞队列,可以头尾操作. */
  protected LinkedBlockingDeque<T> queue;
  /** 队列中需要至少flushCount个元素,默认100,消费者线程才会触发业务逻辑. */
  private int flushCount = Constants.FLUSH_COUNT;
  /** 队列初始容量默认1000000(Integer.MAX_VALUE,本打算无限制的),否则很容易丢失数据,因为向队列插入元素时,如果队列满了,会把最先放入的元素删除. */
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

  /**
   * 创建一个容量capacity的队列,并指定flushCount.
   *
   * <p>Another description after blank line.
   *
   * @param target 队列名.
   * @param capacity 队列容量.
   * @param flushCount 批量刷新一次大小.
   * @author admin
   */
  protected AbstractQueue(final int capacity, final int flushCount, final String target) {
    this.target = target;
    this.capacity = capacity;
    this.flushCount = flushCount;
    this.queue = new LinkedBlockingDeque<>(capacity);
  }

  /**
   * 将数据快速放入列队中
   *
   * <p>Another description after blank line.
   *
   * @return true, 成功放入队列. false,队列满,放入失败
   * @author admin
   */
  public boolean enqueue(final T e) {
    return queue.offer(e);
  }

  /**
   * 将数据快速放入列队中(等待一定时间)
   *
   * <p>Another description after blank line.
   *
   * @param t 放入队列中的元素.
   * @param timeout 最大等待时间.
   * @return true, 成功放入队列. false,队列满,放入失败
   * @author admin
   */
  public boolean enqueue(final T t, final long timeout) {
    boolean isSuccess = false;
    try {
      isSuccess = queue.offer(t, timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, "放入队列时出现异常.", e);
      Thread.currentThread().interrupt();
    }
    return isSuccess;
  }

  /**
   * 创建一个消费者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public ConsumerRunnable createConsumerRunnable() {
    return new ConsumerRunnable();
  }

  /**
   * 创建一个生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public ProducerRunnable createProducerRunnable(final T record) {
    return new ProducerRunnable(record);
  }

  /**
   * 创建一个生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public StudyHandler<T> createProducerWorker() {
    return new ProducerWorker();
  }

  /**
   * 实时获取队列元素数量.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public int size() {
    return this.queue.size();
  }

  /**
   * 消费者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ConsumerRunnable implements Runnable {

    /**
     * 在执行业务之前,进行检查.
     *
     * <p>Another description after blank line.
     *
     * @author admin
     */
    @Override
    public void run() {
      try {
        // 重新获取队列元素数.
        int size = size();
        // 如果队列为空,不执行业务.
        if (size != 0) {
          // 如果元素数大于flushCount(默认100),则每次获取100条.否则直接获取全部元素.
          process(Math.min(size, flushCount));
        }
      } catch (InterruptedException e) {
        LOGGER.log(Level.SEVERE, "执行业务时出现异常.", e);
        // 中断异常需要手动标记.
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * 生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ProducerRunnable implements Runnable {

    /** 生产一个元素. */
    protected T record;

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @param record 放入队列中的元素.
     * @author admin
     */
    public ProducerRunnable(final T record) {
      this.record = record;
    }

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @author admin
     */
    @Override
    public void run() {
      // 如果队列满了.
      while (!enqueue(record)) {
        // 删除最先加入的一个元素.
        queue.pollFirst();
      }
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ProducerWorker implements StudyHandler<T> {

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @param record 放入队列中的元素.
     * @author admin
     */
    @Override
    public void handle(final T record) {
      // 如果队列满了.
      while (!enqueue(record)) {
        // 删除最先加入的一个元素.
        queue.pollFirst();
      }
    }
  }
}
