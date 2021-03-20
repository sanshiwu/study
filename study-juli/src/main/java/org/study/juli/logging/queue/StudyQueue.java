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
   * @param record 向队列生产一个元素.
   * @return Runnable 创建一个生产者线程任务.
   * @author admin
   */
  Runnable createProducerRunnable(T record);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return ProducerWorker 创建一个生产者线程任务.
   * @author admin
   */
  StudyHandler<T> createProducerWorker();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Runnable 创建一个消费者线程任务.
   * @author admin
   */
  Runnable createConsumerRunnable();

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
   * @param size 一次性处理多少条日志.
   * @throws InterruptedException 抛出线程中断异常.
   * @author admin
   */
  void process(int size) throws InterruptedException;
}
