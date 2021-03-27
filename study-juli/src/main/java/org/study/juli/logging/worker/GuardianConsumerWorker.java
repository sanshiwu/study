package org.study.juli.logging.worker;

import java.util.logging.Handler;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.handler.FileHandler;
import org.study.juli.logging.handler.FileHandlerV2;
import org.study.juli.logging.queue.FileQueue;
import org.study.juli.logging.queue.StudyHandler;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class GuardianConsumerWorker implements StudyHandler<Handler> {

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
        FileQueue fileQueue = value.fileQueue;
        int size = fileQueue.size();
        if (size != 0) {
          // 一次处理5000条.
          value.process(size);
        }
      }
    }
    if (handler instanceof FileHandlerV2) {
      final FileHandlerV2 value = (FileHandlerV2) handler;
      // 得到处理器最后一条日志消息的时间.
      long sys = value.getSys();
      // 得到当前系统的时间.
      long c = System.currentTimeMillis();
      // 如果当前处理器最后一条日志的时间不为空(为空代表没有接收到日志消息),并且距离当前时刻,系统时间超过了2秒.
      if (sys != 0L && (c - sys) > Constants.MAX_FREE_TIME) {
        FileQueue fileQueue = value.fileQueue;
        int size = fileQueue.size();
        if (size != 0) {
          // 一次处理5000条.
          value.process(size);
        }
      }
    }
  }
}
