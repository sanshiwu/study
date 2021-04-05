package org.study.juli.examples.kafka;

import java.util.concurrent.TimeUnit;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.queue.AbstractQueue;
import org.study.juli.logging.queue.StudyHandler;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-04 14:09
 * @since 2021-04-04 14:09:00
 */
public class KafkaQueue extends AbstractQueue<LogRecord> {

  public KafkaQueue(String target) {
    super(target);
  }

  public KafkaQueue(int capacity, String target) {
    super(capacity, target);
  }


  /**
   * 将数据快速放入列队中
   *
   * <p>Another description after blank line.
   *
   * @return true, 成功放入队列. false,队列满,放入失败
   * @author admin
   */
  public boolean enqueue(final LogRecord e) {
    // 使用非阻塞方法将元素插入队列.
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
  public boolean enqueue(final LogRecord t, final long timeout) {
    boolean isSuccess = false;
    try {
      // 使用非阻塞方法将元素插入队列.
      isSuccess = queue.offer(t, timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return isSuccess;
  }

  /**
   * 创建一个生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public ProducerRunnable createProducerRunnable(final LogRecord record) {
    return new ProducerRunnable(record);
  }

  /**
   * 创建一个生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public StudyHandler<LogRecord> createProducerWorker() {
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
   * 生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ProducerRunnable implements Runnable {

    /** 生产一个元素. */
    protected LogRecord record;

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @param record 放入队列中的元素.
     * @author admin
     */
    public ProducerRunnable(final LogRecord record) {
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
  public class ProducerWorker implements StudyHandler<LogRecord> {

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @param record 放入队列中的元素.
     * @author admin
     */
    @Override
    public void handle(final LogRecord record) {
      // 如果队列满了.
      while (!enqueue(record)) {
        // 删除最先加入的一个元素.
        queue.pollFirst();
      }
    }
  }
}
