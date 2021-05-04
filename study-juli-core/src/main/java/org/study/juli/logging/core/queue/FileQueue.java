package org.study.juli.logging.core.queue;

import java.util.concurrent.TimeUnit;
import org.study.juli.logging.api.metainfo.LogRecord;
import org.study.juli.logging.api.worker.StudyWorker;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class FileQueue extends AbstractQueue<LogRecord> {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param target .
   * @author admin
   */
  public FileQueue(final String target) {
    super(target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param capacity .
   * @param target .
   * @author admin
   */
  public FileQueue(final int capacity, final String target) {
    super(capacity, target);
  }

  /**
   * 将数据快速放入列队中
   *
   * <p>Another description after blank line.
   *
   * @param e .
   * @author admin
   */
  public void enqueue(final LogRecord e) {
    try {
      // 使用阻塞方法将元素插入队列. 天然的背压方式,当队列满后阻塞.
      queue.put(e);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    }
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
      // 使用阻塞方法将元素插入队列.
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
   * @param logRecord .
   * @return ProducerRunnable .
   * @author admin
   */
  public ProducerRunnable createProducerRunnable(final LogRecord logRecord) {
    return new ProducerRunnable(logRecord);
  }

  /**
   * 创建一个生产者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @return StudyHandler LogRecord .
   * @author admin
   */
  public StudyWorker<LogRecord> createProducerWorker() {
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
    protected LogRecord logRecord;

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @param logRecord 放入队列中的元素.
     * @author admin
     */
    public ProducerRunnable(final LogRecord logRecord) {
      this.logRecord = logRecord;
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
      enqueue(logRecord);
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ProducerWorker implements StudyWorker<LogRecord> {

    /**
     * This is a method description.
     *
     * <p>Another description after blank line.
     *
     * @param logRecord 放入队列中的元素.
     * @author admin
     */
    @Override
    public void handle(final LogRecord logRecord) {
      enqueue(logRecord);
    }
  }
}