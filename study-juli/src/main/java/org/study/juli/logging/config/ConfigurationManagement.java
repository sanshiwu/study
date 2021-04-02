package org.study.juli.logging.config;

import java.util.Map;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.logger.JuliLogger;
import org.study.juli.logging.manager.AbstractLogManager;
import org.study.juli.logging.manager.ClassLoaderLogInfo;

/**
 * 提供一个接口,动态修改Logger的配置.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ConfigurationManagement implements Management {
  private final AbstractLogManager logManager = AbstractLogManager.getLogManager();
  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public ConfigurationManagement() {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handlerName 创建handler.
   * @author admin
   */
  @Override
  public void createHandler(final String handlerName) {
    // 1.直接调用注册处理器方法,因为使用的是当前类加载器加载类,所以可以加载任意的新增加的class.基于这个可以动态增加第三方的处理器.
    logManager.registerHandler(getClassLoaderLogInfo(), handlerName);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void deleteHandler(final String handlerName) {
    // 可能给handler增加状态.
    // 1.先删除所有Logger下的Handler,解除处理器对象的引用.
    // 2.关闭当前Handler,把遗留数据刷到磁盘.
    // 3.等待GC回收handler完毕,结束删除流程.
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void updateHandler(final String handlerName) {
    // 可能给handler增加状态.
    // 1.先删除所有Logger下的Handler,解除处理器对象的引用.
    // 2.关闭当前Handler,把遗留数据刷到磁盘.
    // 3.等待GC回收handler完毕,结束删除流程.
    // 4.重新注册handler.
    // 5.为所有Logger添加新的handler.
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void closeHandler(final String handlerName) {
    // 可能给handler增加状态.
    // 1.先删除所有Logger下的Handler,解除处理器对象的引用.
    // 2.关闭当前Handler,把遗留数据刷到磁盘.
    // 3.等待GC回收handler完毕,结束删除流程.
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public Handler getHandler(final String handlerName) {
    return handlers().get(handlerName);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public JuliLogger getLogger(final String loggerName) {
    return loggers().get(loggerName);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public Map<String, JuliLogger> loggers() {
    return getClassLoaderLogInfo().loggers;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public Map<String, Handler> handlers() {
    return getClassLoaderLogInfo().handlers;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public ClassLoaderLogInfo getClassLoaderLogInfo() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    return logManager.classLoaderLoggers.get(classLoader);
  }
}
