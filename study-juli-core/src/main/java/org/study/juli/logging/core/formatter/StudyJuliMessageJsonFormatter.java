package org.study.juli.logging.core.formatter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.study.juli.logging.api.metainfo.Constants;
import org.study.juli.logging.api.metainfo.LogRecord;
import org.study.juli.logging.core.manager.AbstractLogManager;

/**
 * 日志文本行Json格式化,扩展JDK提供的简单格式化.
 *
 * <p>按行输出纯json格式的消息.
 *
 * @author admin
 */
public class StudyJuliMessageJsonFormatter extends AbstractMessageFormatter {
  /** . */
  private final DateTimeFormatter pattern;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public StudyJuliMessageJsonFormatter() {
    // 获取当前处理器配置的格式化.
    String timeFormat =
        AbstractLogManager.getLogManager()
            .getProperty(
                StudyJuliMessageJsonFormatter.class.getName() + Constants.DATETIME_FORMAT_NAME);
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
  public StudyJuliMessageJsonFormatter(final String timeFormat) {
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
   * @param logRecord 需要格式化的消息.
   * @return 返回格式化后的消息.
   * @author admin
   */
  @Override
  public String format(final LogRecord logRecord) {
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(logRecord.getInstant(), ZoneOffset.UTC);
    // 日期格式化.
    final String format = this.pattern.format(zdt);
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(this.inQuotes("timestamp") + ": ");
    sb.append(this.inQuotes(format));
    sb.append(",");
    sb.append(this.inQuotes("level") + ": ");
    sb.append(this.inQuotes(logRecord.getLevel().getName()));
    sb.append(",");
    sb.append(this.inQuotes("thread") + ": ");
    sb.append(this.inQuotes(Thread.currentThread().getName()));
    sb.append(",");
    sb.append(this.inQuotes("fullClassPath") + ": ");
    sb.append(this.inQuotes(logRecord.getSourceClassName()));
    sb.append(",");
    sb.append(this.inQuotes("method") + ": ");
    sb.append(this.inQuotes(logRecord.getSourceMethodName()));
    sb.append(",");
    sb.append(this.inQuotes("lineNumber") + ": ");
    sb.append(this.inQuotes(logRecord.getLineNumber() + ""));
    sb.append(",");
    if (checkUnique()) {
      sb.append(this.inQuotes("uniqueId") + ": ");
      sb.append(this.inQuotes(logRecord.getUniqueId()));
      sb.append(",");
    }
    sb.append(this.inQuotes("serialNumber") + ": ");
    sb.append(logRecord.getSerialNumber());
    sb.append(",");
    // 日志自定义字段.
    final Map<String, String> customs = logRecord.getCustoms();
    for (final Entry<String, String> entry : customs.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      sb.append(this.inQuotes(key) + ": ");
      sb.append(this.inQuotes(value));
      sb.append(",");
    }
    // 首先兼容JDK原生的日志格式,然后进行格式化处理.
    final String message = defaultFormat(logRecord);
    sb.append(this.inQuotes("message") + ": ");
    sb.append(this.inQuotes(message));
    // 如果有异常堆栈信息,则打印出来.
    final Throwable thrown = logRecord.getThrown();
    if (thrown != null) {
      sb.append(",");
      sb.append(this.inQuotes("stacktrace") + ": ");
      sb.append("[");
      sb.append(this.inQuotes(thrown.toString()));
      sb.append(",");
      String separator = "";
      StackTraceElement[] stackTraceElements = thrown.getStackTrace();
      for (StackTraceElement stackTraceElement : stackTraceElements) {
        sb.append(separator);
        sb.append(this.inQuotes(stackTraceElement.toString()));
        separator = ",";
      }
      sb.append("]");
    }
    sb.append("}");
    // 增加一个换行符号(按照平台获取)
    sb.append(System.lineSeparator());
    return sb.toString();
  }

  /**
   * This is a method description.
   *
   * @param s This is a param description.
   * @return This is a return description.
   * @author admin
   */
  public String inQuotes(final String s) {
    return "\"" + s + "\"";
  }
}
