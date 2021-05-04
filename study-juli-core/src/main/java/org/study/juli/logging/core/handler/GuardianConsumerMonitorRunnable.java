package org.study.juli.logging.core.handler;

import java.util.Map;
import java.util.Map.Entry;
import org.study.juli.logging.api.context.WorkerContext;
import org.study.juli.logging.api.handler.Handler;
import org.study.juli.logging.api.logger.Logger;
import org.study.juli.logging.api.metainfo.Level;
import org.study.juli.logging.core.manager.AbstractLogManager;
import org.study.juli.logging.core.manager.ClassLoaderLogInfo;
import org.study.juli.logging.core.manager.JuliLogger;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class GuardianConsumerMonitorRunnable implements Runnable {
  /** . */
  private static final Logger LOGGER =
      JuliLogger.getLogger(GuardianConsumerMonitorRunnable.class.getName());
  /** . */
  private final WorkerContext context;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param context .
   * @author admin
   */
  public GuardianConsumerMonitorRunnable(final WorkerContext context) {
    this.context = context;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void run() {
    try {
      // 得到全局唯一的日志管理器,自定义管理器.
      AbstractLogManager logManager = AbstractLogManager.getLogManager();
      // 得到当前的类加载器.
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      // 当前类加载器对象的日志信息.
      ClassLoaderLogInfo info = logManager.classLoaderLoggers.get(classLoader);
      // 得到所有的处理器.
      Map<String, Handler> handlers = info.handlers;
      // 循环所有的处理器.
      for (Entry<String, Handler> entry : handlers.entrySet()) {
        // 将处理器转成FileHandler(目前只支持FileHandler,后面会支持KafkaHandler).
        Handler handler = entry.getValue();
        // 将处理器提交给线程池.
        context.executeInExecutorService(handler, new GuardianConsumerWorker());
      }
    } catch (Exception e) {
      LOGGER.logp(
          Level.SEVERE, GuardianConsumerMonitorRunnable.class.getName(), "run", "守护消费监听线程出现异常", e);
    }
  }
}
