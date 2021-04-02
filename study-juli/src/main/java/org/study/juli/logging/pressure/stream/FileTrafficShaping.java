package org.study.juli.logging.pressure.stream;

import java.util.ArrayDeque;
import java.util.logging.LogRecord;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-03-24 22:01
 * @since 2021-03-24 22:01:00
 */
public class FileTrafficShaping extends AbstractTrafficShaping {
  private final ArrayDeque<LogRecord> messagesQueue = new ArrayDeque<>();
  private long queueSize;

  public FileTrafficShaping(long writeLimit, long readLimit, long checkInterval, long maxTime) {
   //
  }

  public FileTrafficShaping(long writeLimit, long readLimit, long checkInterval) {
   //
  }

  public FileTrafficShaping(long writeLimit, long readLimit) {
   //
  }

  public FileTrafficShaping(long checkInterval) {
    //
  }
}
