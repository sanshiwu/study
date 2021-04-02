package org.study.juli.logging.base;

import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.util.Optional;
import java.util.ServiceLoader;
import org.study.juli.logging.exception.StudyJuliConfigException;
import org.study.juli.logging.spi.JuliLog;
import org.study.juli.logging.spi.Log;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public final class LogFactory implements Factory {
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
      // spi加载
      serviceLoad();
    } catch (Exception e) {
      throw new StudyJuliConfigException(e);
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
      Optional<Log> first = logLoader.findFirst();
      Class<? extends Log> logClass = null;
      if (first.isPresent()) {
        Log log = first.get();
        logClass = log.getClass();
        // 创建构造函数对象.
        spiConstructor = logClass.getConstructor(String.class);
      }
    } else {
      // 如果没有发现配置项. 则创建一个默认的Logging对象.
      spiConstructor = JuliLog.class.getConstructor(String.class);
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
