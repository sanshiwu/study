package org.study.juli.logging.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import org.study.juli.logging.exception.StudyJuliRuntimeException;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class KafkaQueue extends AbstractQueue<LogRecord> {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param target 列队名字.
   * @author admin
   */
  public KafkaQueue(final String target) {
    super(target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param capacity 列队容量.
   * @param target   列队名字.
   * @author admin
   */
  public KafkaQueue(final int capacity, final String target) {
    super(capacity, target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param capacity   列队容量.
   * @param flushCount 刷新批量.
   * @param target     列队名字.
   * @author admin
   */
  public KafkaQueue(final int capacity, final int flushCount, final String target) {
    super(capacity, flushCount, target);
  }

  /**
   * 处理数据,向kafka生产数据.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void process(final int size) throws InterruptedException {
    try {
      List<LogRecord> list = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        LogRecord logRecord = queue.poll();
        if (logRecord == null) {
          break;
        }
        list.add(logRecord);
      }
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    }
  }
}
