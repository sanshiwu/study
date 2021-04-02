package org.study.juli.logging.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.logger.JuliLogger;
import org.study.juli.logging.logger.RootLogger;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ClassLoaderLogInfo {

  /** 这个rootLogger是为了给系统初始化时的类使用的. */
  public final RootLogger rootLogger = new RootLogger();
  /** 保存系统加载时所有的Logger,包括study_juli本身,study_juli依赖的库.以及引用study_juli的程序. */
  public final Map<String, JuliLogger> loggers = new ConcurrentHashMap<>(1000);
  /** 保存系统加载时所有的Handler,包括公用的以及自定义的. */
  public final Map<String, Handler> handlers = new HashMap<>(32);
  /** 保存系统加载时配置信息. */
  public final Properties props = new Properties();

  /**
   * 向rootLogger添加公用的处理器.
   *
   * <p>study_juli本身,study_juli依赖的库使用.
   *
   * <p>引用study_juli的程序使用""通用Logger.
   *
   * @param handler 通用的处理器.
   * @author admin
   */
  public void addHandler(final Handler handler) {
    // 向rootLogger添加公用的处理器.
    rootLogger.addHandler(handler);
  }

  public void setLevel(String name) {
    rootLogger.setLevel(Level.findLevel(name));
  }
}
