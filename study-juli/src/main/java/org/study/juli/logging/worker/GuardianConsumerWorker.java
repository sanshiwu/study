package org.study.juli.logging.worker;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.handler.FileHandler;
import org.study.juli.logging.queue.StudyHandler;
import org.study.juli.logging.queue.StudyQueue;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class GuardianConsumerWorker implements StudyHandler<Handler> {

  /** . */
  private static final Logger LOGGER = Logger.getLogger(GuardianConsumerWorker.class.getName());

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void handle(final Handler handler) {
    if (handler instanceof FileHandler) {
      final FileHandler value = (FileHandler) handler;
      // 得到处理器最后一条日志消息的时间.
      long sys = value.getSys();
      // 得到当前系统的时间.
      long c = System.currentTimeMillis();
      // 如果当前处理器最后一条日志的时间不为空(为空代表没有接收到日志消息),并且距离当前时刻,系统时间超过了2秒.
      if (sys != 0L && (c - sys) > Constants.MAX_FREE_TIME) {
        // 得到当前处理器的消息队列.
        final StudyQueue<LogRecord> queue = value.getAbstractQueue();
        try {
          // 一次处理5000条.
          queue.process(Constants.BATCH_SIZE);
        } catch (InterruptedException e) {
          // Restore interrupted state...
          LOGGER.log(Level.SEVERE, "守护消费监听线程执行业务时出现异常", e);
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
