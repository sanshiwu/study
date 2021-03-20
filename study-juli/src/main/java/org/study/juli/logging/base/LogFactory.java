package org.study.juli.logging.base;

import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.util.ServiceLoader;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.study.juli.logging.exception.StudyJuliConfigException;
import org.study.juli.logging.exception.StudyJuliRuntimeException;
import org.study.juli.logging.logger.Logging;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
@SuppressWarnings({"java:S3776", "java:S2658"})
public final class LogFactory {

  /** 单例的日志实例工厂. */
  private static final LogFactory SINGLETON = new LogFactory();
  /** SPI 构造函数对象. */
  private Constructor<? extends Log> spiConstructor;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private LogFactory() {
    try {
      // 原本初始化方法放在构造函数内, 但是出现了加载顺序的问题.现在放在第一次访问时初始化.
      // 目的是为了解决,任意依赖的jar中日志都可以输出.
      FileSystems.getDefault();
      // 如果没有配置文件,则用默认的格式化.
      configFile();
      // spi加载
      serviceLoad();
    } catch (Exception e) {
      throw new StudyJuliConfigException(e);
    }
  }

  private static void configFile() {
    try {
      final String configFile = System.getProperty(Constants.CONFIG_FILE);
      if (configFile == null) {
        // 获取格式化类的全名,如果为空则用默认的.
        final String formatter = System.getProperty(Constants.FORMATTER_NAME, Constants.FORMATTER);
        // 使用自定义的格式化. 初始化一个格式化器,代替JDK默认的丑陋的格式化.
        Formatter fmt = (Formatter) Class.forName(formatter).getConstructor().newInstance();
        // 获取默认的Logger,名字是""字符串.
        Handler handler = Logger.getLogger("").getHandlers()[0];
        if (handler instanceof ConsoleHandler) {
          handler.setFormatter(fmt);
        }
      }
    } catch (Exception e) {
      throw new StudyJuliRuntimeException("初始化格式化器异常", e);
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param clazz 使用类的简单名称作为日志的标志.
   * @return 返回一个Log对象.
   * @author admin
   */
  public static Log getLog(final Class<?> clazz) {
    final String name = clazz.getName();
    return SINGLETON.getInstance(name);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name 使用简单名称作为日志的标志.
   * @return 返回一个Log对象.
   * @author admin
   */
  public static Log getLog(final String name) {
    return SINGLETON.getInstance(name);
  }

  private void serviceLoad() throws NoSuchMethodException {
    // 读取SPI,获取第三方日志的LOGGER.
    final ServiceLoader<Log> logLoader = ServiceLoader.load(Log.class);
    // 是否读取到了SPI配置项.
    if (logLoader.iterator().hasNext()) {
      // 得到对象的Class对象.获取第一条配置项,如果为空则创建一个Logging对象.
      final Class<? extends Log> logClass = logLoader.findFirst().orElse(new Logging()).getClass();
      // 创建构造函数对象.
      spiConstructor = logClass.getConstructor(String.class);
    } else {
      // 如果没有发现配置项. 则创建一个默认的Logging对象.
      spiConstructor = Logging.class.getConstructor(String.class);
      // 构造内部的配置异常消息.
      StudyJuliConfigException exception =
          new StudyJuliConfigException(Constants.JULI_CONFIG_EXCEPTION_MESSAGE);
      // 主动打印出异常栈,java:S1148 规则弃用,可以使用.
      exception.printStackTrace();
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name 使用简单名称作为日志的标志.
   * @return 返回一个Log对象.
   * @author admin
   */
  private Log getInstance(final String name) {
    try {
      return spiConstructor.newInstance(name);
    } catch (final Exception e) {
      throw new StudyJuliConfigException(e);
    }
  }
}
