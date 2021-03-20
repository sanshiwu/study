package org.study.juli.logging.handler;

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.queue.FileQueue;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
@SuppressWarnings({"java:S3776", "java:S2658", "java:S1699"})
public class FileHandler extends AbstractHandler {

  /** 使用自定义的通用的日志管理器. */
  private static final Logger LOGGER = Logger.getLogger(FileHandler.class.getName());
  /** . */
  private final FileQueue fileQueue;
  /** . */
  private final Runnable consumerRunnable;
  /** . */
  private String suffix;
  /** . */
  private String prefix;
  /** . */
  private String directory;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileHandler() {
    // 为日志消息创建一个队列.
    fileQueue = new FileQueue("FileQueue");
    consumerRunnable = fileQueue.createConsumerRunnable();
    // 将队列传递给通用的处理器.
    super.setAbstractQueue(fileQueue);
    try {
      // 读取日志配置文件,初始化配置.
      configure();
    } catch (Exception e) {
      // 处理所有异常.
      LOGGER.log(Level.SEVERE, "处理器配置异常:", e);
    }
    // 开始创建文件流,用于日志写入.
    fileQueue.open();
  }

  /**
   * 每向队列中产生一条日志,会触发flush这个方法.
   *
   * <p>关于读写锁,参考JDK ReentrantReadWriteLock第137行例子.
   *
   * @author admin
   */
  @Override
  public void flush() {
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    String current = intervalFormatter.format(zdt);
    // 首先加一个读锁.
    readLock.lock();
    try {
      long currentLong = Long.parseLong(current);
      // 检查当前日期和上一次的日期.如果不相等,需要重新打开一个新的日志文件.
      if (checkState(currentLong)) {
        LOGGER.log(Level.INFO, "当前系统的日期改变,需要转换到新的一天,前一天的日期:{0}", initialization);
        // 释放读锁.
        readLock.unlock();
        // 加一个写锁.
        writeLock.lock();
        try {
          // 重新检查状态.
          if (checkState(currentLong)) {
            // 关闭流之前,消费掉队列里面的全部数据.
            fileQueue.process(Constants.BATCH_SIZE);
            // 关闭原来的文件流.
            fileQueue.close();
            // 改变日志interval标志.
            initialization = currentLong;
            // 将interval传递给队列对象.
            fileQueue.setLogFilePath(getFile());
            // 重新打开新的的文件流.
            fileQueue.open();
          }
        } catch (InterruptedException e) {
          // Restore interrupted state...
          LOGGER.log(Level.SEVERE, "转换日志文件处理业务时出现异常:", e);
          Thread.currentThread().interrupt();
        } finally {
          // 加一个读锁.
          readLock.lock();
          // 释放写锁.
          writeLock.unlock();
        }
      }
      // 具体业务逻辑.
      LOG_CONSUMER_CONTEXT.executeInExecutorService(consumerRunnable);
    } finally {
      // 释放读锁.
      readLock.unlock();
    }
  }

  /**
   * 读写锁状态检查,日志文件按照日期进行切换的条件.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private boolean checkState(final long current) {
    // 文件翻转开关打开并且当前系统时间减去初始化的时间大于间隔时间,即可进行翻转日志文件.
    return rotatable && (current - initialization) >= interval;
  }

  /**
   * 配置方法.
   *
   * <p>Another description after blank line.
   *
   * @throws Exception 抛出所有异常.
   * @author admin
   */
  @Override
  public void configure() throws Exception {
    LOGGER.log(Level.INFO, "当前处理器:{0}", fileQueue);
    // 是否按照天,进行切分日志.
    String rotatable = getProperty(".rotatable", "true");
    LOGGER.log(Level.INFO, "当前处理器日志文件开关:{0}", rotatable);
    // 日志文件翻转开关.
    this.rotatable = Boolean.parseBoolean(rotatable);
    // 设置日志文件翻转开关.
    this.directory = getProperty(".directory", "logs");
    // 设置日志文件目录.
    this.prefix = getProperty(".prefix", "study_juli.");
    // 设置日志文件前缀.
    this.suffix = getProperty(".suffix", ".log");
    // 设置日志文件后缀.
    // 设置日志文件翻转间隔.
    interval = Integer.valueOf(getProperty(".interval", "1"));
    LOGGER.log(Level.INFO, "当前处理器日志文件翻转间隔:{0}", interval);
    // 设置日志文件翻转间隔格式化.
    intervalFormatter = DateTimeFormatter.ofPattern(getProperty(".intervalFormatter", "yyyyMMdd"));
    LOGGER.log(Level.INFO, "当前处理器日志文件翻转间隔格式化:{0}", intervalFormatter);
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    // 设置处理器创建时当前的系统时间.
    initialization = Long.valueOf(intervalFormatter.format(zdt));
    LOGGER.log(Level.INFO, "当前处理器启动时的系统时间:{0}", initialization);
    // 设置处理器创建时当前的系统时间.
    // 设置日志文件的编码.
    setEncoding(getProperty(".encoding", "UTF-8"));
    LOGGER.log(Level.INFO, "当前处理器日志编码:{0}", "UTF-8");
    // 设置日志文件的级别.
    setLevel(Level.parse(getProperty(".level", "" + Level.ALL)));
    LOGGER.log(Level.INFO, "当前处理器日志级别:{0}", "ALL");
    // 设置日志文件的过滤器.
    String filterName = getProperty(".filter", "org.study.juli.logging.filter.StudyJuliFilter");
    LOGGER.log(Level.INFO, "当前处理器日志过滤器:{0}", filterName);
    // 获取当前类加载器.
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    // 设置过滤器.
    setFilter((Filter) cl.loadClass(filterName).getConstructor().newInstance());
    // 获取日志格式化器.
    String formatterName =
        getProperty(".formatter", "org.study.juli.logging.formatter.StudyJuliMessageFormatter");
    LOGGER.log(Level.INFO, "当前处理器日志格式化器:{0}", formatterName);
    // 设置日志格式化器.
    setFormatter((Formatter) cl.loadClass(formatterName).getConstructor().newInstance());
    // 设置日志格式化器.
    fileQueue.setFormatter(getFormatter());
    // 设置写锁.
    fileQueue.setWriteLock(writeLock);
    // 设置日志错误管理器.
    setErrorManager(new ErrorManager());
    // 日志的文件对象.
    fileQueue.setLogFilePath(getFile());
  }

  private File getFile() {
    File dir = new File(this.directory);
    // 定位到日志绝对路径.
    File logAbsoluteFile = dir.getAbsoluteFile();
    if (!logAbsoluteFile.exists()) {
      logAbsoluteFile.mkdirs();
    }
    // 日志文件名.
    String logFileName = this.prefix + (this.rotatable ? this.initialization : "") + this.suffix;
    // 得到日志的完整路径.
    return new File(logAbsoluteFile, logFileName);
  }
}
