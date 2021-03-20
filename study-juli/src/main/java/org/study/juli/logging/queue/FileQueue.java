package org.study.juli.logging.queue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.study.juli.logging.base.Constants;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
@SuppressWarnings({"java:S2093"})
public class FileQueue extends AbstractQueue<LogRecord> {
  /** . */
  private static final Logger LOGGER = Logger.getLogger(FileQueue.class.getName());
  /** . */
  private Formatter formatter;
  /** 写锁. */
  private Lock writeLock;
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

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileQueue(final String target) {
    super(target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileQueue(final int capacity, final String target) {
    super(capacity, target);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public FileQueue(final int capacity, final int flushCount, final String target) {
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
        // 10,TimeUnit.MILLISECONDS,不等待.
        LogRecord logRecord = queue.poll();
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
      LOGGER.log(Level.SEVERE, "Print Writer向文件写入日志异常.", e);
    }
  }

  /**
   * 创建文件流.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public void open() {
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
      LOGGER.log(Level.SEVERE, "创建Print Writer对象异常.", e);
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
      // 尝试关闭文件流.
      if (fileStream != null) {
        try {
          fileStream.flush();
          fileStream.close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "关闭File Stream对象异常.", e);
        }
      }
      // 尝试关闭buff文件流.
      if (bufferedStream != null) {
        try {
          bufferedStream.flush();
          bufferedStream.close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "关闭Buffered Stream对象异常.", e);
        }
      }
      // 尝试关闭stream writer流.
      if (streamWriter != null) {
        try {
          streamWriter.flush();
          streamWriter.close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "关闭Stream Stream对象异常.", e);
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
  public void setLogFilePath(final File logFilePath) {
    this.logFilePath = logFilePath;
  }
}
