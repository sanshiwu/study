package org.study.juli.logging.runnable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.study.juli.logging.context.WorkerContext;
import org.study.juli.logging.manager.ClassLoaderLogInfo;
import org.study.juli.logging.manager.StudyJuliLogManager;
import org.study.juli.logging.worker.GuardianConsumerWorker;

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
      Logger.getLogger(GuardianConsumerMonitorRunnable.class.getName());

  private final WorkerContext context;

  public GuardianConsumerMonitorRunnable(WorkerContext context) {
    this.context = context;
  }

  @Override
  public void run() {
    try {
      // 得到全局唯一的日志管理器,自定义管理器.
      StudyJuliLogManager logManager = (StudyJuliLogManager) LogManager.getLogManager();
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
      LOGGER.log(Level.SEVERE, "守护消费监听线程出现异常", e);
    }
  }
}
