package org.study.juli.logging.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.study.juli.logging.api.handler.Handler;
import org.study.juli.logging.api.logger.Logger;
import org.study.juli.logging.api.metainfo.Level;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ClassLoaderLogInfo {

  /** 这个rootLogger是为了给系统初始化时的类使用的. */
  public final Logger rootLogger = new RootLogger();
  /** 保存系统加载时所有的Logger,包括study_juli本身,study_juli依赖的库.以及引用study_juli的程序. */
  public final Map<String, Logger> loggers = new ConcurrentHashMap<>(1000);
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

  /**
   * .
   *
   * <p>.
   *
   * @param name .
   * @author admin
   */
  public void setLevel(final String name) {
    rootLogger.setLevel(Level.findLevel(name));
  }
}
