package org.study.juli.logging.filter;

import org.study.juli.logging.core.LogRecord;

/**
 * 对日志进行过滤.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class StudyJuliFilter implements Filter {

  /**
   * 默认空的日志拦截器.
   *
   * <p>Another description after blank line.
   *
   * @param record 消息记录.
   * @return boolean false代表日志消息丢弃.
   * @author admin
   */
  @Override
  public boolean isLoggable(final LogRecord record) {
    return true;
  }
}
