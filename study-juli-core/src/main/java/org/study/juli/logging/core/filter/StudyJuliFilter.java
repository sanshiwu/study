package org.study.juli.logging.core.filter;

import org.study.juli.logging.api.filter.Filter;
import org.study.juli.logging.api.metainfo.LogRecord;

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
   * @param logRecord 消息记录.
   * @return boolean false代表日志消息丢弃.
   * @author admin
   */
  @Override
  public boolean isLoggable(final LogRecord logRecord) {
    return true;
  }
}
