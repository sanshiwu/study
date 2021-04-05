package org.study.juli.logging.utils;

import java.util.Map;
import org.study.juli.logging.manager.AbstractLogManager;
import org.study.juli.logging.manager.ClassLoaderLogInfo;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-05 09:19
 * @since 2021-04-05 09:19:00
 */
public class LogManagerUtils {

  public static String getProperty(String key, String value) {
    // 获取全局的日志管理器.
    final AbstractLogManager manager = AbstractLogManager.getLogManager();
    // 获取日志管理器存储的所有ClassLoader.
    final Map<ClassLoader, ClassLoaderLogInfo> classLoaderLoggers = manager.classLoaderLoggers;
    // 获取当前的系统类加载器.
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    // 获取当前系统类加载器的日志信息.
    final ClassLoaderLogInfo classLoaderLogInfo = classLoaderLoggers.get(classLoader);
    return classLoaderLogInfo.props.getProperty(key, value);
  }
}
