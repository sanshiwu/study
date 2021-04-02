package org.study.juli.logging.base;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public final class Constants {

  public static final String LEVEL = ".level";
  /** . */
  public static final String HANDLERS = ".handlers";
  /** . */
  public static final String HANDLERS_SINGLE = "handlers";
  /** 日志消息中有多个大括号对. */
  public static final String BRACE = "{}";
  /** . */
  public static final String FORMATTER =
      "org.study.juli.logging.formatter.StudyJuliMessageFormatter";
  /** . */
  public static final String FORMATTER_NAME = "org.study.juli.logging.handler.ConsoleHandler.formatter";
  /** . */
  public static final String CONFIG_FILE = "org.study.juli.logging.config.file";
  /** . */
  public static final String LOG_MANAGER = "org.study.juli.logging.manager";
  /** . */
  public static final String STUDY_JULI_LOG_MANAGER = "org.study.juli.logging.manager.StudyJuliLogManager";
  /** . */
  public static final int STACK_TRACE_ELEMENT = 3;
  /** . */
  public static final int LOOP_COUNT = 5;
  /** . */
  public static final String DATETIME_FORMAT_NAME = ".dateTimeFormat";
  /** . */
  public static final String DATETIME_FORMAT_VALUE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  /** . */
  public static final int BATCH_SIZE = 500;
  /** . */
  public static final String JULI_CONFIG_EXCEPTION_MESSAGE =
      "SPI服务没有读取到任何实现,调试查看BuiltinClassLoader类1045行checkURL方法.";
  /** . */
  public static final long INITIAL_DELAY = 5000L;
  /** . */
  public static final long PERIOD = 5000L;
  /** . */
  public static final long MAX_EXEC_TIME = 10000L;
  /** . */
  public static final long MAX_FREE_TIME = 2000L;
  /** . */
  public static final int BATCH_BUF_SIZE = 8192;
  /** . */
  public static final int FLUSH_COUNT = 100;
  /** . */
  public static final int LEN_COUNT = 100;
  /** . */
  public static final int CAPACITY = 1000000;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private Constants() {
    //
  }
}
