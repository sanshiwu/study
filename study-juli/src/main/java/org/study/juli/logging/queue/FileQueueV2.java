package org.study.juli.logging.queue;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class FileQueueV2 extends AbstractQueue<LogRecord> {
  /** . */
  private static final Logger LOGGER = Logger.getLogger(FileQueueV2.class.getName());

  /** . */
  private String date = "";
  /** . */
  private String directory;
  /** . */
  private String prefix;
  /** . */
  private String suffix;
  /** . */
  private boolean rotatable = true;
  /** . */
  private Formatter formatter;
  /** 读锁. */
  private Lock readLock;
  /** 写锁. */
  private Lock writeLock;
  /** . */
  private BufferedWriter bufferedWriter;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileQueueV2(final String target) {
    super(target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileQueueV2(final int capacity, final String target) {
    super(capacity, target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileQueueV2(final int capacity, final int flushCount, final String target) {
    super(capacity, flushCount, target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void process(final int size) throws InterruptedException {
    try {
      boolean flag = false;
      // 获取一批数据,写入磁盘.
      for (int i = 0; i < size; i++) {
        // 10, TimeUnit.MILLISECONDS,不等待.
        LogRecord logRecord = queue.poll();
        // 如果数量不够,导致从队列获取空对象.
        if (logRecord != null) {
          flag = true;
          bufferedWriter.write(formatter.format(logRecord));
        } else {
          break;
        }
      }
      if (flag) {
        // 刷新一次IO磁盘.
        bufferedWriter.flush();
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "刷新一次IO磁盘异常.", e);
    }
  }

  /**
   * 创建kafka客户端.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void open() {
    writeLock.lock();
    try {
      Path directoryPath = Paths.get(directory);
      if (!Files.exists(directoryPath)) {
        Files.createDirectories(directoryPath);
      }
      String logFileName = prefix + (rotatable ? date : "") + suffix;
      Path filePath = Paths.get(logFileName);
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
      bufferedWriter =
          Files.newBufferedWriter(
              directoryPath.resolve(filePath),
              StandardCharsets.UTF_8,
              StandardOpenOption.CREATE,
              StandardOpenOption.WRITE,
              StandardOpenOption.APPEND);
      bufferedWriter.write("");
    } catch (Exception e) {
      // 如何任何阶段发生了异常,主动关闭所有IO资源.
      LOGGER.log(Level.SEVERE, "创建bufferedWriter异常.", e);
      close();
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void close() {
    writeLock.lock();
    try {
      // 尝试关闭buffered writer流.
      if (bufferedWriter != null) {
        bufferedWriter.write("");
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedWriter = null;
        date = "";
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "关闭bufferedWriter异常.", e);
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public Lock getReadLock() {
    return this.readLock;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setReadLock(final Lock readLock) {
    this.readLock = readLock;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public Lock getWriteLock() {
    return this.writeLock;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setWriteLock(final Lock writeLock) {
    this.writeLock = writeLock;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public Formatter getFormatter() {
    return this.formatter;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setFormatter(final Formatter formatter) {
    this.formatter = formatter;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public String getDate() {
    return this.date;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setDate(final String date) {
    this.date = date;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public String getDirectory() {
    return this.directory;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setDirectory(final String directory) {
    this.directory = directory;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public String getPrefix() {
    return this.prefix;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public String getSuffix() {
    return this.suffix;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setSuffix(final String suffix) {
    this.suffix = suffix;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public boolean isRotatable() {
    return this.rotatable;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void setRotatable(final boolean rotatable) {
    this.rotatable = rotatable;
  }
}
