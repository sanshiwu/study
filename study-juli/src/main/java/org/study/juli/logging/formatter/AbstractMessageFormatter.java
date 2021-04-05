package org.study.juli.logging.formatter;

import java.text.MessageFormat;
import java.util.Map;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.manager.AbstractLogManager;
import org.study.juli.logging.manager.ClassLoaderLogInfo;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-03-27 10:50
 * @since 2021-03-27 10:50:00
 */
public abstract class AbstractMessageFormatter implements Formatter {

  protected AbstractMessageFormatter() {
    //
  }

  /**
   * 兼容JDK原生的日志格式.
   *
   * <p>对新的日志格式来说,性能上几乎0损耗,运行1亿方法,耗时5毫秒.
   *
   * @author admin
   */
  protected static String defaultFormat(final LogRecord record) {
    String message = record.getMessage();
    final Object[] parameters = record.getParameters();
    if (parameters != null && parameters.length != 0) {
      int index = -1;
      final int fence = message.length() - 1;
      while ((index = message.indexOf('{', index + 1)) > -1) {
        if (index >= fence) {
          break;
        }
        final char digit = message.charAt(index + 1);
        if (digit >= '0' && digit <= '9') {
          message = MessageFormat.format(message, parameters);
        }
      }
    }
    return message;
  }

  /**
   * 一亿次调用大概2.5秒.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  protected static boolean checkUnique() {
    AbstractLogManager manager = AbstractLogManager.getLogManager();
    Map<ClassLoader, ClassLoaderLogInfo> classLoaderLoggers = manager.classLoaderLoggers;
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    ClassLoaderLogInfo classLoaderLogInfo = classLoaderLoggers.get(classLoader);
    final String unique = classLoaderLogInfo.props.getProperty(Constants.UNIQUE, Constants.FALSE);
    return Boolean.parseBoolean(unique);
  }
}
