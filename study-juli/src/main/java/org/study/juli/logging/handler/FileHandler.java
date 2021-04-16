package org.study.juli.logging.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.study.juli.logging.base.Constants;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.exception.StudyJuliRuntimeException;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.formatter.Formatter;
import org.study.juli.logging.queue.FileQueue;
import org.study.juli.logging.strategy.DeleteStrategy;
import org.study.juli.logging.strategy.GzipCompressStrategy;
import org.study.juli.logging.strategy.StrategyFileFilter;
import org.study.juli.logging.utils.ClassLoadingUtils;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
@SuppressWarnings({"java:S2093"})
public class FileHandler extends AbstractHandler {
  /** . */
  private final Runnable consumerRunnable = new ConsumerRunnable();
  /** . */
  private final GzipCompressStrategy gzipCompressStrategy = new GzipCompressStrategy();
  /** . */
  private final DeleteStrategy deleteStrategy = new DeleteStrategy();
  /** . */
  private final LinkedHashMap<String, File> logFiles = new LinkedHashMap<>(16);
  /** . */
  private final FileQueue fileQueue;
  /** . */
  private String suffix;
  /** . */
  private String prefix;
  /** . */
  private String directory;
  /** . */
  private PrintWriter writer;
  /** . */
  private FileOutputStream fileStream;
  /** . */
  private BufferedOutputStream bufferedStream;
  /** . */
  private OutputStreamWriter streamWriter;
  /** . */
  private File logFilePath;
  /** . */
  private File logAbsolutePath;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileHandler() {
    try {
      // 读取日志配置文件,初始化配置.
      config();
      // 动态配置队列属性.
      fileQueue = new FileQueue(prefix);
      producerWorker = fileQueue.createProducerWorker();
      // 开始创建文件流,用于日志写入.
      open();
    } catch (Exception e) {
      // 处理所有异常.
      throw new StudyJuliRuntimeException(e);
    }
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
        // 释放读锁.
        readLock.unlock();
        // 加一个写锁.
        writeLock.lock();
        try {
          // 重新检查状态.
          if (checkState(currentLong)) {
            // 关闭流之前,消费掉队列里面的全部数据.
            process(Constants.BATCH_SIZE);
            // 关闭原来的文件流.
            closeIo();
            // 改变日志interval标志.
            initialization = currentLong;
            // 将interval传递给队列对象.
            logFilePath = getFile();
            // 重新打开新的的文件流.
            open();
            // 执行策略.
            strategy();
          }
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
   * 进行文件切换的瞬间,必须进行背压控制,否则内存溢出.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private void strategy() {
    // 获取要压缩和删除的文件.
    int size = logFiles.size();
    if (size > Constants.LOOP_COUNT) {
      for (int i = 0; i < size - Constants.LOOP_COUNT; i++) {
        Entry<String, File> next = logFiles.entrySet().iterator().next();
        String key = next.getKey();
        File source = logFiles.get(key);
        String path = source.getAbsolutePath();
        File destination = new File(path + ".gz");
        // 执行压缩.
        gzipCompressStrategy.execute(source, destination, 1);
        // 执行删除.
        deleteStrategy.delete(source);
        // 删除条目.
        logFiles.remove(key);
      }
    }
  }

  /**
   * JDK会调用这个方法.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void publish(final LogRecord record) {
    // 记录当前处理器最后一次处理日志的时间.
    sys = System.currentTimeMillis();
    // 全局处理器日志消息数量.
    GLOBAL_COUNTER.incrementAndGet();
    // 单个处理器日志消息数量.
    counter.incrementAndGet();
    // 得到当前处理器的日志级别.
    Level level = this.getLevel();
    // 处理器可以处理日志的级别.
    final int levelValue = level.intValue();
    // 用户发送日志的级别.
    int recordLevel = record.getLevel().intValue();
    // 如果日志的消息级别,比当前处理器的级别小则不处理日志. 如果当前处理器关闭日志级别,处理器也不处理日志.
    if (recordLevel < levelValue || levelValue == Level.OFF.intValue()) {
      return;
    }
    // 获取当前处理器的日志过滤器.
    Filter filter = this.getFilter();
    // 如果过滤器返回false,当前日志消息丢弃.
    if (!filter.isLoggable(record)) {
      return;
    }
    // 启动一个线程,开始生产日志.(考虑将LogRecord预先格式化成字符串消息,LogRecord对象生命周期结束.)
    LOG_PRODUCER_CONTEXT.executeInExecutorService(record, producerWorker);
    // 如果队列容量大于等于5000,通知消费者消费.如果此时生产者不再生产数据,则队列中会有<5000条数据永久存在,因此需要启动一个守护者线程GUARDIAN处理.
    int size = fileQueue.size();
    // 当前处理器的队列中日志消息达到5000条,处理一次.
    if (size >= Constants.BATCH_SIZE) {
      // 提交一个任务,用于通知消费者线程去消费队列数据.
      LOG_PRODUCER_NOTICE_CONSUMER_CONTEXT.executeInExecutorService(
          this, producerNoticeConsumerWorker);
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
  private void config() throws Exception {
    // 是否按照天,进行切分日志.
    String rotatable = getProperty(".rotatable", "true");
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
    interval = Integer.parseInt(getProperty(".interval", "1"));
    // 设置日志文件翻转间隔格式化.
    intervalFormatter = DateTimeFormatter.ofPattern(getProperty(".intervalFormatter", "yyyyMMdd"));
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    // 设置处理器创建时当前的系统时间.
    initialization = Long.parseLong(intervalFormatter.format(zdt));
    // 设置处理器创建时当前的系统时间.
    // 设置日志文件的编码.
    setEncoding(getProperty(".encoding", "UTF-8"));
    // 设置日志文件的级别.
    setLevel(Level.findLevel(getProperty(".level", "" + Level.ALL)));
    // 设置日志文件的过滤器.
    String filterName = getProperty(".filter", Constants.FILTER);
    // 设置过滤器.
    Constructor<?> filterConstructor = ClassLoadingUtils.constructor(filterName);
    setFilter((Filter) filterConstructor.newInstance());
    // 获取日志格式化器.
    String formatterName = getProperty(".formatter", Constants.FORMATTER);
    // 设置日志格式化器.
    Constructor<?> formatterConstructor = ClassLoadingUtils.constructor(formatterName);
    setFormatter((Formatter) formatterConstructor.newInstance());
    // 设置日志格式化器.
    formatter = getFormatter();
    // 日志子目录.
    logAbsolutePath = getDirectory();
    // 日志的文件对象.
    logFilePath = getFile();
  }

  private File getDirectory() {
    File path = new File(this.directory + File.separator + this.prefix);
    if (!path.exists()) {
      boolean make = path.mkdirs();
      if (!make) {
        throw new StudyJuliRuntimeException("目录创建异常.");
      }
    }
    if (path.isDirectory()) {
      File[] files = path.listFiles(new StrategyFileFilter());
      if (files != null) {
        for (File file : files) {
          logFiles.putIfAbsent(file.getName(), file);
        }
      }
    }
    return path;
  }

  private File getFile() {
    // 日志文件名.
    String logFileName = this.prefix + (this.rotatable ? this.initialization : "") + this.suffix;
    // 得到日志的完整路径.
    File file = new File(logAbsolutePath, logFileName);
    logFiles.putIfAbsent(logFileName, file);
    return file;
  }

  /**
   * 消费者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ConsumerRunnable implements Runnable {

    /**
     * 在执行业务之前,进行检查.
     *
     * <p>Another description after blank line.
     *
     * @author admin
     */
    @Override
    public void run() {
      // 重新获取队列元素数.
      int size = fileQueue.size();
      // 如果队列为空,不执行业务.
      if (size != 0) {
        // 如果元素数大于flushCount(默认100),则每次获取100条.否则直接获取全部元素.
        process(Math.min(size, Constants.FLUSH_COUNT));
      }
    }
  }

  public void process(final int size) {
    try {
      boolean flag = false;
      // 获取一批数据,写入磁盘.
      for (int i = 0; i < size; i++) {
        // 非阻塞方法获取队列元素.
        LogRecord logRecord = fileQueue.poll();
        // 如果数量不够,导致从队列获取空对象.
        if (logRecord != null) {
          // 设置不为空的标志.
          flag = true;
          // 需要加写锁,可能会关闭.
          if (writer != null) {
            // 写入缓存(如果在publish方法中先格式化,则性能下降30%,消费端瓶颈取决于磁盘IO,生产端速度达不到最大,并发不够).
            writer.write(formatter.format(logRecord));
          }
        } else {
          break;
        }
      }
      // 如果缓存中由数据,刷新一次.
      if (flag && writer != null) {
        // 刷新一次IO磁盘.
        writer.flush();
      }
    } catch (Exception e) {
      // ignore Exception.
      throw new StudyJuliRuntimeException(e);
    }
  }

  private void open() {
    writeLock.lock();
    try {
      // java:S2093 这个严重问题,暂时无法解决,先忽略sonar的警告.因为文件不能关闭,需要长时间打开.但是sonar检测,需要关闭IO资源.
      fileStream = new FileOutputStream(logFilePath, true);
      // 创建一个buffered流,缓存大小默认8192.
      bufferedStream = new BufferedOutputStream(fileStream, Constants.BATCH_BUF_SIZE);
      // 创建一个输出流,使用UTF-8 编码.
      streamWriter = new OutputStreamWriter(bufferedStream, StandardCharsets.UTF_8);
      // 创建一个PrintWriter,启动自动刷新.
      writer = new PrintWriter(streamWriter, true);
      // 尝试写入一个空"".
      writer.write("");
    } catch (Exception e) {
      // 如何任何阶段发生了异常,主动关闭所有IO资源.
      closeIo();
      throw new StudyJuliRuntimeException(e);
    } finally {
      writeLock.unlock();
    }
  }

  public void closeIo() {
    writeLock.lock();
    try {
      // 尝试关闭文件流.
      if (fileStream != null) {
        try {
          fileStream.flush();
          fileStream.close();
        } catch (IOException e) {
          throw new StudyJuliRuntimeException(e);
        }
      }
      // 尝试关闭buff文件流.
      if (bufferedStream != null) {
        try {
          bufferedStream.flush();
          bufferedStream.close();
        } catch (IOException e) {
          throw new StudyJuliRuntimeException(e);
        }
      }
      // 尝试关闭stream writer流.
      if (streamWriter != null) {
        try {
          streamWriter.flush();
          streamWriter.close();
        } catch (IOException e) {
          throw new StudyJuliRuntimeException(e);
        }
      }
      // 尝试关闭print writer流.
      if (writer != null) {
        writer.write("");
        writer.flush();
        writer.close();
        writer = null;
      }
    } finally {
      writeLock.unlock();
    }
  }

  public FileQueue getFileQueue() {
    return fileQueue;
  }
}
