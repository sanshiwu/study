package org.study.juli.logging.manager;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.exception.StudyJuliRuntimeException;

/**
 * 增强JDK默认的日志管理.
 *
 * <p>Another description after blank line.
 *
 * <p>Classes should not be loaded dynamically(动态加载类).
 *
 * @author admin
 */
@SuppressWarnings({"java:S3776", "java:S2658"})
public class StudyJuliLogManager extends LogManager {

  /** 自定义类加载器,目前只支持系统类加载器,不支持自定义类加载器. */
  public final Map<ClassLoader, ClassLoaderLogInfo> classLoaderLoggers = new WeakHashMap<>(1);
  /** 给线程绑定一个线程变量,目前阿里插件扫描到没有调用remove方法释放,实际已经调用. */
  private final ThreadLocal<String> prefix = new ThreadLocal<>();
  /** 自定义锁,用于在对配置文件CRUD时锁定业务方法. */
  private final ReentrantLock configurationLock = new ReentrantLock();

  /**
   * Logger代表使用日志的类,比如在类中创建日志对象,并打印日志消息,那么这个类就叫Logger.
   *
   * <p>这个方法需要仔细研究,重构,目前业务逻辑清晰,但是代码很糟糕.
   *
   * @author admin
   */
  @Override
  public synchronized boolean addLogger(final Logger logger) {
    // 得到类全路径,org.study.juli.example.Examples .
    final String loggerName = logger.getName();
    // 得到类包路径,org.study.juli.example .
    String packageName = "";
    // 获取这个类 当前的包名.
    int dotIndex = loggerName.lastIndexOf('.');
    // 如果当前Logger名使用的是类的全路径.
    if (dotIndex >= 0) {
      // 获取到当前Logger名中包的最长路径(去掉类的最简单名).
      packageName = loggerName.substring(0, dotIndex);
    }
    // 得到当前的类加载器.
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    // 当前类加载器对象的日志信息.
    final ClassLoaderLogInfo info = classLoaderLoggers.get(classLoader);
    // 1.首先看这个类以前是不是加过.
    if (info.loggers.containsKey(loggerName)) {
      // 2.如果加过,直接跳过.
      return false;
    } else {
      // 3.如果没加过.首先看类配置没有,如果配置不看包.
      boolean extracted = extracted(logger, classLoader, loggerName);
      // 如果配置项存在当前Logger的名.
      if (extracted) {
        // 直接注册这个logger.
        info.loggers.put(loggerName, logger);
        // 4.如果配置项不存在当前Logger的名,使用包路径,再试试.
      } else {
        // 首先查看包是不是加载路.
        if (info.loggers.containsKey(packageName)) {
          // 如果包加载过.
          final Logger packageNameLogger = info.loggers.get(packageName);
          // 获取到这个包下的所有处理器.
          final Handler[] handlers = packageNameLogger.getHandlers();
          // 循环每个处理器.
          for (final Handler h : handlers) {
            // 将包的处理器全部添加到当前Logger.
            logger.addHandler(h);
          }
          // 将当前的日志级别设置成包的日志级别.
          logger.setLevel(packageNameLogger.getLevel());
          // 添加当前Logger.
          info.loggers.put(loggerName, logger);
        } else {
          // 首先查看包没有加载路.首先尝试获取包的配置项.
          boolean extracted1 = extracted(logger, classLoader, packageName);
          // 如果成功获取了包的配置项目.
          if (extracted1) {
            // 添加当前Logger.
            info.loggers.put(packageName, logger);
          } else {
            // 5.如果包没配置,使用默认的全局的.获取全局的Logger.
            final Logger rootLogger = info.loggers.get("");
            // 看看rootLogger是否存在.
            if (rootLogger != null) {
              // 如果存在,获取rootLogger的所有处理器.
              final Handler[] rootHandlers = rootLogger.getHandlers();
              // 循环rootLogger的所有处理器.
              for (final Handler rootHandler : rootHandlers) {
                // 添加每一个处理器到当前Logger中.
                logger.addHandler(rootHandler);
              }
              // 将当前的日志级别设置成包的日志级别.
              logger.setLevel(rootLogger.getLevel());
            } else {
              // 如果当前rootLogger是空的,则添加系统rootLogger(我们自己定义的rootLogger,不是JDK logging自己的rootLogger).
              logger.setParent(info.rootLogger);
            }
            // 添加当前Logger.
            info.loggers.put(loggerName, logger);
          }
        }
      }
    }
    return true;
  }

  /**
   * 方法需要重构,暂时不处理.
   *
   * <p>Another description after blank line.
   *
   * @param logger logger.
   * @param classLoader classLoader.
   * @param loggerName loggerName.
   * @return boolean.
   * @author admin
   */
  private boolean extracted(
      final Logger logger, final ClassLoader classLoader, final String loggerName) {
    // 获取当前类加载器下存储的信息.
    final ClassLoaderLogInfo info = classLoaderLoggers.get(classLoader);
    // 获取当前Logger的日志级别.
    final String levelString = getProperty(loggerName + ".level");
    // 如果日志级别没配置,直接采用全局配置的日志级别.
    if (levelString != null && !"".equals(levelString)) {
      // 设置日志级别到当前Logger.
      logger.setLevel(Level.parse(levelString.trim()));
    } else {
      // 获取全局的Logger.
      final Logger rootLogger = info.loggers.get("");
      if (rootLogger != null) {
        // 将当前的日志级别设置成包的日志级别.
        logger.setLevel(rootLogger.getLevel());
      }
    }
    // 获取日志处理器,并注册到当前logger,如果没有日志处理器,可以考虑当前包的日志处理器.如果包也没有配置,则考虑使用全局配置的处理器.
    final String handlers = getProperty(loggerName + Constants.HANDLERS);
    if (handlers != null) {
      // 默认当前Logger会继承父类的.但是这个handlers处理全局handlers配置,不需要继承.
      logger.setUseParentHandlers(false);
      // 得到所有的全局handlers处理器.
      final StringTokenizer tok = new StringTokenizer(handlers, ",");
      // 获取每一个全局handlers.
      while (tok.hasMoreTokens()) {
        // 获取handler的处理器名称.
        final String handlerName = (tok.nextToken().trim());
        // 根据handler名获取处理器.
        final Handler handler = info.handlers.get(handlerName);
        // 如果全局处理器不为空,添加这个全局处理器到当前Logger.
        if (handler != null) {
          logger.addHandler(handler);
        }
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @return Logger
   * @author admin
   */
  @Override
  public synchronized Logger getLogger(final String name) {
    // 获取当前的系统类加载器.
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    // 根据类加载器获取类加载器下的loggers,然后得到对应name下的Logger.
    return classLoaderLoggers.get(classLoader).loggers.get(name);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public synchronized Enumeration<String> getLoggerNames() {
    // 获取当前的系统类加载器.
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    // 根据类加载器获取类加载器下的loggers.
    return Collections.enumeration(classLoaderLoggers.get(classLoader).loggers.keySet());
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public String getProperty(final String name) {
    // 得到当前处理器的前缀.
    final String prefixTem = this.prefix.get();
    // 拼接前缀+配置项的名.
    String prefixName = name;
    if (prefixTem != null) {
      prefixName = prefixTem + name;
    }
    return findProperty(prefixName);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name name.
   * @return String.
   * @author admin
   */
  private synchronized String findProperty(final String name) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    ClassLoaderLogInfo info = classLoaderLoggers.get(classLoader);
    String result = info.props.getProperty(name);
    if ((result == null) && (info.props.isEmpty())) {
      ClassLoader current = classLoader.getParent();
      while (current != null) {
        info = classLoaderLoggers.get(current);
        if (info != null) {
          result = info.props.getProperty(name);
          if ((result != null) || (!info.props.isEmpty())) {
            break;
          }
        }
        current = current.getParent();
      }
      if (result == null) {
        result = super.getProperty(name);
      }
    }
    return result;
  }

  /**
   * 读取日志的配置文件,日志管理器必须和日志文件一起配置.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException 由JDK框架处理.
   * @author admin
   */
  @Override
  public void readConfiguration() throws SecurityException {
    // 获取日志配置文件.
    final String configFileStr = System.getProperty("java.util.logging.config.file");
    // 如果配置为空,直接异常处理.
    if (configFileStr == null) {
      throw new StudyJuliRuntimeException("没有配置日志文件,-Djava.util.logging.config.file.");
    }
    // 获取当前执行线程的类加载器.
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final ClassLoaderLogInfo info = new ClassLoaderLogInfo();
    classLoaderLoggers.put(classLoader, info);
    final Path target = new File(configFileStr).toPath();
    // 读取日志Properties文件,然后关闭IO,try(){}语法会自动关闭.
    configurationLock.lock();
    try (InputStream inputStream = Files.newInputStream(target)) {
      info.props.load(inputStream);
    } catch (Exception e) {
      throw new StudyJuliRuntimeException("自定义配置文件加载异常.", e);
    } finally {
      configurationLock.unlock();
    }
    // 如果是系统类加载器.
    if (classLoader != ClassLoader.getSystemClassLoader()) {
      // 自定义类加载器不支持.
      throw new StudyJuliRuntimeException("不支持自定义类加载器加载.");
    }
    // 获取全局的.handlers.
    final String rootHandlers = info.props.getProperty(Constants.HANDLERS);
    // 注册.handlers.
    this.handler(classLoader, info, rootHandlers, true);
    // 获取单个类或者单个包handlers.
    final String handlers = info.props.getProperty(Constants.HANDLERS_SINGLE);
    // 注册handlers.
    this.handler(classLoader, info, handlers, false);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param classLoader .
   * @param info .
   * @param handlers .
   * @param flag .
   * @author admin
   */
  private void handler(
      final ClassLoader classLoader,
      final ClassLoaderLogInfo info,
      final String handlers,
      final boolean flag) {
    if (handlers != null) {
      final StringTokenizer tok = new StringTokenizer(handlers, ",");
      // 循环获取每一个handler,并得到前缀和handlers的className.
      while (tok.hasMoreTokens()) {
        final String handlerName = (tok.nextToken().trim());
        final Handler handler = registerHandler(classLoader, info, handlerName);
        if (flag) {
          info.addHandler(handler);
        }
      }
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param classLoader .
   * @param info .
   * @param handlerName .
   * @return Handler 注册的处理器.
   * @author admin
   */
  public Handler registerHandler(
      final ClassLoader classLoader, final ClassLoaderLogInfo info, final String handlerName) {
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
        final Handler handler =
            (Handler) classLoader.loadClass(handlerClassName).getConstructor().newInstance();
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

  /**
   * 有JDK框架调用,当线程执行完毕,会调用reset方法.
   *
   * <p>覆盖父类的reset方法.父类的方法会执行一系列的操作..
   *
   * @throws SecurityException 由JDK框架处理.
   * @author admin
   */
  @Override
  public void reset() throws SecurityException {
    // 覆盖父类方法,清空当前类加载器对应的INFO消息.
  }
}
