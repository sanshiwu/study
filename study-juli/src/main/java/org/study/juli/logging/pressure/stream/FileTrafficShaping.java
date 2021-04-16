package org.study.juli.logging.pressure.stream;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
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

  protected FileTrafficShaping(long writeLimit, long readLimit, long checkInterval, long maxTime) {
    super(writeLimit, readLimit, checkInterval, maxTime);
  }

  @Override
  void submitWrite(Object msg, long size, long delay, long now) {
    synchronized (this) {
      if (delay == 0 && messagesQueue.isEmpty()) {
        trafficCounter.bytesRealWriteFlowControl(size);

        return;
      }
      messagesQueue.addLast(null);
      queueSize += size;
      checkWriteSuspend(delay, queueSize);
    }
  }

  public void handlerAdded() throws Exception {
    TrafficCounter trafficCounter = new TrafficCounter(this, null, "ChannelTC" +
        null, checkInterval);
    setTrafficCounter(trafficCounter);
    trafficCounter.start();
  }

  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    trafficCounter.stop();
    // write order control
    synchronized (this) {
        for (LogRecord toSend : messagesQueue) {
          long size = calculateSize(toSend);
          trafficCounter.bytesRealWriteFlowControl(size);
          queueSize -= size;
        }
    }
    releaseWriteSuspended();
    releaseReadSuspended(null);
  }

  public long queueSize() {
    return queueSize;
  }
}
