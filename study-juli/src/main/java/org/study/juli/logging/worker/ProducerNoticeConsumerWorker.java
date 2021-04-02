package org.study.juli.logging.worker;

import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.queue.StudyHandler;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ProducerNoticeConsumerWorker implements StudyHandler<Handler> {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void handle(final Handler handler) {
    handler.flush();
  }
}
