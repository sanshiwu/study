package org.study.juli.logging.core.handler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import org.study.juli.logging.api.exception.StudyJuliRuntimeException;
import org.study.juli.logging.api.filter.Filter;
import org.study.juli.logging.api.formatter.Formatter;
import org.study.juli.logging.api.metainfo.Constants;
import org.study.juli.logging.api.metainfo.Level;
import org.study.juli.logging.api.metainfo.LogRecord;
import org.study.juli.logging.core.utils.ClassLoadingUtils;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ConsoleHandler extends AbstractHandler {
  /** . */
  private PrintWriter writer;
  /** . */
  private OutputStreamWriter streamWriter;

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public ConsoleHandler() {
    // 读取日志配置文件,初始化配置.
    config();
    // 开始创建文件流,用于日志写入.
    open();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param logRecord .
   * @author admin
   */
  @Override
  public void publish(final LogRecord logRecord) {
    // 记录当前处理器最后一次处理日志的时间.
    sys = System.currentTimeMillis();
    GLOBAL_COUNTER.incrementAndGet();
    counter.incrementAndGet();
    // 得到当前处理器的日志级别.
    Level level = this.getLevel();
    // 处理器可以处理日志的级别.
    final int levelValue = level.intValue();
    // 用户发送日志的级别.
    int recordLevel = logRecord.getLevel().intValue();
    // 如果日志的消息级别,比当前处理器的级别小则不处理日志. 如果当前处理器关闭日志级别,处理器也不处理日志.
    if (recordLevel < levelValue || levelValue == Level.OFF.intValue()) {
      return;
    }
    // 获取当前处理器的日志过滤器.
    Filter filter = this.getFilter();
    // 如果过滤器返回false,当前日志消息丢弃.
    if (!filter.isLoggable(logRecord)) {
      return;
    }
    String msg = getFormatter().format(logRecord);
    writer.write(msg);
    writer.flush();
  }

  @Override
  public void flush() {
    //
  }

  @SuppressWarnings({"java:S2093", "java:S106"})
  private void open() {
    writeLock.lock();
    try {
      // 创建一个输出流,使用UTF-8 编码.
      streamWriter = new OutputStreamWriter(System.err, StandardCharsets.UTF_8);
      // 创建一个PrintWriter,启动自动刷新.
      writer = new PrintWriter(streamWriter, true);
      // 尝试写入一个空"".
      writer.write("");
    } catch (Exception e) {
      // 如何任何阶段发生了异常,主动关闭所有IO资源.
      closeIo();
      throw new StudyJuliRuntimeException(e);
    } finally {
      writeLock.unlock();
    }
  }

  private void config() {
    try {
      // 设置日志文件的编码.
      setEncoding(getProperty(".encoding", "UTF-8"));
      // 设置日志文件的级别.
      setLevel(Level.findLevel(getProperty(".level", "" + Level.ALL)));
      // 设置日志文件的过滤器.
      String filterName = getProperty(".filter", Constants.FILTER);
      // 设置过滤器.
      Constructor<?> filterConstructor = ClassLoadingUtils.constructor(filterName);
      setFilter((Filter) ClassLoadingUtils.newInstance(filterConstructor));
      // 获取日志格式化器.
      String formatterName = getProperty(".formatter", Constants.FORMATTER);
      // 设置日志格式化器.
      Constructor<?> formatterConstructor = ClassLoadingUtils.constructor(formatterName);
      setFormatter((Formatter) ClassLoadingUtils.newInstance(formatterConstructor));
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    }
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void closeIo() {
    writeLock.lock();
    try {
      // 尝试关闭stream writer流.
      if (streamWriter != null) {
        try {
          streamWriter.flush();
          streamWriter.close();
        } catch (IOException e) {
          throw new StudyJuliRuntimeException(e);
        }
      }
      // 尝试关闭print writer流.
      if (writer != null) {
        writer.write("");
        writer.flush();
        writer.close();
        writer = null;
      }
    } finally {
      writeLock.unlock();
    }
  }
}
