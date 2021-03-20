package org.study.juli.logging.handler;

import org.study.juli.logging.exception.StudyJuliRuntimeException;
import org.study.juli.logging.queue.KafkaQueue;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
@SuppressWarnings({"java:S3776", "java:S2658", "java:S1699"})
public class KafkaHandler extends AbstractHandler {

  /** kafka消息内存队列. */
  private final KafkaQueue kafkaQueue;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public KafkaHandler() {
    kafkaQueue = new KafkaQueue("KafkaQueue");
    super.setAbstractQueue(kafkaQueue);
    try {
      configure();
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    }
  }

  /**
   * 每向队列中产生一条日志,会触发flush这个方法.
   *
   * <p>关于读写锁,参考JDK ReentrantReadWriteLock第137行例子.
   *
   * @author admin
   */
  @Override
  public void flush() {
    //
  }

  /**
   * 配置方法.
   *
   * <p>Another description after blank line.
   *
   * @throws Exception 抛出所有异常.
   * @author admin
   */
  @Override
  public void configure() throws Exception {
    //
  }
}
