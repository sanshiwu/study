package org.study.juli.logging.formatter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.manager.AbstractLogManager;
import org.study.juli.logging.utils.LogManagerUtils;

/**
 * 日志文本行格式化,扩展JDK提供的简单格式化.
 *
 * <p>按行输出纯文本的消息.
 *
 * @author admin
 */
public class StudyJuliMessageTextFormatter extends AbstractMessageFormatter {
  /** . */
  private final DateTimeFormatter pattern;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public StudyJuliMessageTextFormatter() {
    // 获取当前处理器配置的格式化.
    String timeFormat =
        AbstractLogManager.getLogManager()
            .getProperty(
                StudyJuliMessageTextFormatter.class.getName() + Constants.DATETIME_FORMAT_NAME);
    // 如果为空.
    if (Objects.isNull(timeFormat)) {
      // 使用默认的格式化.
      timeFormat = Constants.DATETIME_FORMAT_VALUE;
    }
    // 创建一个日期时间格式化实例.
    pattern = DateTimeFormatter.ofPattern(timeFormat);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param timeFormat 日期格式化.
   * @author admin
   */
  public StudyJuliMessageTextFormatter(final String timeFormat) {
    // 如果为空.
    if (Objects.isNull(timeFormat)) {
      // 使用默认的格式化.
      pattern = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_VALUE);
    } else {
      // 使用自定义的格式化.
      pattern = DateTimeFormatter.ofPattern(timeFormat);
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param record 需要格式化的消息.
   * @return 返回格式化后的消息.
   * @author admin
   */
  @Override
  public String format(final LogRecord record) {
    // 首先兼容JDK原生的日志格式,然后进行格式化处理.
    String message = defaultFormat(record);
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(record.getInstant(), ZoneOffset.UTC);
    // 日期格式化.
    final String format = this.pattern.format(zdt);
    // 组装完整的日志消息.100->16试试看.
    final StringBuilder sb = new StringBuilder(16);
    // 拼接日期时间格式化.
    sb.append(format);
    sb.append(' ');
    // 日志级别.
    sb.append(record.getLevel().getName());
    sb.append(' ');
    sb.append('[');
    // 当前执行的线程名.
    sb.append(Thread.currentThread().getName());
    sb.append(']');
    sb.append(' ');
    // 日志由哪个类打印的.
    sb.append(record.getSourceClassName());
    sb.append(' ');
    // 日志由哪个方法打印的.
    sb.append(record.getSourceMethodName());
    sb.append(' ');
    // 日志方法行.
    sb.append(record.getLineNumber());
    sb.append(' ');
    final String unique = LogManagerUtils.getProperty(Constants.UNIQUE, Constants.FALSE);
    if (unique.equals(Constants.TRUE)) {
      // 日志unique id.
      sb.append(record.getUniqueId());
      sb.append(' ');
    }
    // 日志序列号.
    sb.append(record.getSerialNumber());
    sb.append(' ');
    String host = record.getHost();
    if (Objects.nonNull(host)) {
      // 日志进程host.
      sb.append(host);
      sb.append(' ');
    }
    String port = record.getPort();
    if (Objects.nonNull(port)) {
      // 日志进程port.
      sb.append(port);
      sb.append(' ');
    }
    // 日志自定义字段.
    Map<String, String> customs = record.getCustoms();
    for (Entry<String, String> entry : customs.entrySet()) {
      String value = entry.getValue();
      sb.append(value);
      sb.append(' ');
    }
    // 已经格式化后的日志消息.
    sb.append(message);
    // 如果有异常堆栈信息,则打印出来.
    Throwable thrown = record.getThrown();
    if (thrown != null) {
      sb.append(' ');
      sb.append('[');
      sb.append(thrown.toString());
      sb.append(",");
      String separator = "";
      StackTraceElement[] stackTraceElements = thrown.getStackTrace();
      for (StackTraceElement stackTraceElement : stackTraceElements) {
        sb.append(separator);
        sb.append(stackTraceElement.toString());
        separator = ",";
      }
      sb.append(']');
    }
    // 系统换行符.
    sb.append(System.lineSeparator());
    return sb.toString();
  }
}
