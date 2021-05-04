package org.study.juli.logging.core.handler;

import org.study.juli.logging.api.handler.Handler;
import org.study.juli.logging.api.worker.StudyWorker;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ProducerNoticeConsumerWorker implements StudyWorker<Handler> {

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
