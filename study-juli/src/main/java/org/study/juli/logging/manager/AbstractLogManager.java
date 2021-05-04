package org.study.juli.logging.manager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.study.juli.logging.exception.StudyJuliRuntimeException;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.logger.JuliLogger;
import org.study.juli.logging.utils.ClassLoadingUtils;

/**
 * This is a class description(需要优化).
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractLogManager implements LogManager {
  /** . */
  private static final AbstractLogManager ABSTRACT_LOGMANAGER;

  static {
    String logManagerName =
        System.getProperty(Constants.LOG_MANAGER, Constants.STUDY_JULI_LOG_MANAGER);
    Constructor<?> constructor = ClassLoadingUtils.constructor(logManagerName);
    ABSTRACT_LOGMANAGER = (AbstractLogManager) ClassLoadingUtils.newInstance(constructor);
  }

  /** 自定义类加载器,目前只支持系统类加载器,不支持自定义类加载器. */
  public final Map<ClassLoader, ClassLoaderLogInfo> classLoaderLoggers = new WeakHashMap<>(1);
  /** 给线程绑定一个线程变量,目前阿里插件扫描到没有调用remove方法释放,实际已经调用. */
  protected final ThreadLocal<String> prefix = new ThreadLocal<>();
  /** 自定义锁,用于在对配置文件CRUD时锁定业务方法. */
  protected final ReentrantLock configurationLock = new ReentrantLock();
  /** 日志管理器初始化的标识. */
  protected boolean initializationDone;

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return AbstractLogManager.
   * @author admin
   */
  public static AbstractLogManager getLogManager() {
    if (ABSTRACT_LOGMANAGER == null) {
      throw new StudyJuliRuntimeException("");
    }
    ABSTRACT_LOGMANAGER.readConfiguration();
    return ABSTRACT_LOGMANAGER;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return AbstractLogManager.
   * @author admin
   */
  @Override
  public boolean addLogger(final JuliLogger logger) {

    return false;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return AbstractLogManager.
   * @author admin
   */
  @Override
  public JuliLogger getLogger(final String name) {

    return null;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return AbstractLogManager.
   * @author admin
   */
  @Override
  public Enumeration<String> getLoggerNames() {

    return null;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return AbstractLogManager.
   * @author admin
   */
  @Override
  public String getProperty(final String name) {

    return null;
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void readConfiguration() throws SecurityException {
    //
  }

  @Override
  public void readConfiguration(final InputStream ins) throws IOException, SecurityException {
    //
  }

  @Override
  public void reset() throws SecurityException {
    //
  }

  @Override
  public void updateConfiguration(final Function<String, BiFunction<String, String, String>> mapper)
      throws IOException {
    //
  }

  @Override
  public void updateConfiguration(
      final InputStream ins, final Function<String, BiFunction<String, String, String>> mapper)
      throws IOException {
    //
  }

  @Override
  public void checkAccess() throws SecurityException {
    //
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @return AbstractLogManager.
   * @author admin
   */
  @Override
  public AbstractLogManager addConfigurationListener(final Runnable listener) {

    return null;
  }

  @Override
  public void removeConfigurationListener(final Runnable listener) {
    //
  }

  @Override
  public void checkPermission() {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @return JuliLogger
   * @author admin
   */
  public JuliLogger demandLogger(final String name) {
    JuliLogger result = getLogger(name);
    if (result == null) {
      JuliLogger newLogger = new JuliLogger(name, this);
      do {
        if (addLogger(newLogger)) {
          return newLogger;
        }
        result = getLogger(name);
      } while (result == null);
    }
    return result;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param info .
   * @param handlerName .
   * @return Handler 注册的处理器.
   * @author admin
   */
  public Handler registerHandler(final ClassLoaderLogInfo info, final String handlerName) {
    if (!handlerName.isEmpty()) {
      // 如果第一个字符不是数字.
      String handlerClassName = handlerName;
      // 前缀.
      String prefixTem = "";
      // 如果第一个字符是数字.
      if (Character.isDigit(handlerClassName.charAt(0))) {
        // 得到第一个'.'位置.
        int pos = handlerClassName.indexOf('.');
        // 如果找到了.
        if (pos >= 0) {
          // 得到前缀.
          prefixTem = handlerClassName.substring(0, pos + 1);
          // 得到真实的handler className.
          handlerClassName = handlerClassName.substring(pos + 1);
        }
      }
      try {
        // 设置前缀,代表当前处理器配置的前缀.
        this.prefix.set(prefixTem);
        // 反射创建一个处理器.
        Constructor<?> constructor = ClassLoadingUtils.constructor(handlerClassName);
        final Handler handler = (Handler) ClassLoadingUtils.newInstance(constructor);
        // 将处理器添加到系统类加载内.
        info.handlers.putIfAbsent(handlerName, handler);
        return handler;
      } catch (final Exception e) {
        throw new StudyJuliRuntimeException("初始化handler异常.", e);
      } finally {
        // 释放当前前缀.
        this.prefix.remove();
      }
    }
    return null;
  }
}
