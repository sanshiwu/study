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

  private final ArrayDeque<LogRecord> messagesQueue = new ArrayDeque<LogRecord>();

  private long queueSize;

  /**
   * Create a new instance.
   *
   * @param writeLimit 0 or a limit in bytes/s
   * @param readLimit 0 or a limit in bytes/s
   * @param checkInterval The delay between two computations of performances for channels or 0 if no
   *     stats are to be computed.
   * @param maxTime The maximum delay to wait in case of traffic excess.
   */
  public FileTrafficShaping(long writeLimit, long readLimit, long checkInterval, long maxTime) {
    // super(writeLimit, readLimit, checkInterval, maxTime);
  }

  /**
   * Create a new instance using default max time as delay allowed value of 15000 ms.
   *
   * @param writeLimit 0 or a limit in bytes/s
   * @param readLimit 0 or a limit in bytes/s
   * @param checkInterval The delay between two computations of performances for channels or 0 if no
   *     stats are to be computed.
   */
  public FileTrafficShaping(long writeLimit, long readLimit, long checkInterval) {
    // super(writeLimit, readLimit, checkInterval);
  }

  /**
   * Create a new instance using default Check Interval value of 1000 ms and max time as delay
   * allowed value of 15000 ms.
   *
   * @param writeLimit 0 or a limit in bytes/s
   * @param readLimit 0 or a limit in bytes/s
   */
  public FileTrafficShaping(long writeLimit, long readLimit) {
    // super(writeLimit, readLimit);
  }

  /**
   * Create a new instance using default max time as delay allowed value of 15000 ms and no limit.
   *
   * @param checkInterval The delay between two computations of performances for channels or 0 if no
   *     stats are to be computed.
   */
  public FileTrafficShaping(long checkInterval) {
    // super(checkInterval);
  }
/*  public void read(final Object msg) throws Exception {
    long size = calculateSize(msg);
    long now = TrafficCounter.milliSecondFromNano();
    if (size > 0) {
      // compute the number of ms to wait before reopening the channel
      long wait = trafficCounter.readTimeToWait(size, readLimit, maxTime, now);
      wait = checkWaitReadTime(ctx, wait, now);
      if (wait >= MINIMAL_WAIT) { // At least 10ms seems a minimal
        // time in order to try to limit the traffic
        // Only AutoRead AND HandlerActive True means Context Active
        Channel channel = ctx.channel();
        ChannelConfig config = channel.config();
        if (logger.isDebugEnabled()) {
          logger.debug("Read suspend: " + wait + ':' + config.isAutoRead() + ':'
              + isHandlerActive(ctx));
        }
        if (config.isAutoRead() && isHandlerActive(ctx)) {
          config.setAutoRead(false);
          channel.attr(READ_SUSPENDED).set(true);
          // Create a Runnable to reactive the read if needed. If one was create before it will just be
          // reused to limit object creation
          Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
          Runnable reopenTask = attr.get();
          if (reopenTask == null) {
            reopenTask = new ReopenReadTimerTask(ctx);
            attr.set(reopenTask);
          }
          ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
          if (logger.isDebugEnabled()) {
            logger.debug("Suspend final status => " + config.isAutoRead() + ':'
                + isHandlerActive(ctx) + " will reopened at: " + wait);
          }
        }
      }
    }
    informReadOperation(ctx, now);
    ctx.fireChannelRead(msg);
  }

  public void write(final Object msg, final ChannelPromise promise)
      throws Exception {
    long size = calculateSize(msg);
    long now = TrafficCounter.milliSecondFromNano();
    if (size > 0) {
      // compute the number of ms to wait before continue with the channel
      long wait = trafficCounter.writeTimeToWait(size, writeLimit, maxTime, now);
      if (wait >= MINIMAL_WAIT) {
        if (logger.isDebugEnabled()) {
          logger.debug("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':'
              + isHandlerActive(ctx));
        }
        submitWrite(ctx, msg, size, wait, now, promise);
        return;
      }
    }
    // to maintain order of write
    submitWrite(ctx, msg, size, 0, now, promise);
  }

  void submitWrite(final ChannelHandlerContext ctx, final Object msg,
      final long size, final long delay, final long now,
      final ChannelPromise promise) {
    final ToSend newToSend;
    // write order control
    synchronized (this) {
      if (delay == 0 && messagesQueue.isEmpty()) {
        trafficCounter.bytesRealWriteFlowControl(size);
        ctx.write(msg, promise);
        return;
      }
      newToSend = new ToSend(delay + now, msg, promise);
      messagesQueue.addLast(newToSend);
      queueSize += size;
      checkWriteSuspend(ctx, delay, queueSize);
    }
    final long futureNow = newToSend.relativeTimeAction;
    ctx.executor().schedule(new Runnable() {
      @Override
      public void run() {
        sendAllValid(ctx, futureNow);
      }
    }, delay, TimeUnit.MILLISECONDS);
  }

  private void sendAllValid(final ChannelHandlerContext ctx, final long now) {
    // write order control
    synchronized (this) {
      ToSend newToSend = messagesQueue.pollFirst();
      for (; newToSend != null; newToSend = messagesQueue.pollFirst()) {
        if (newToSend.relativeTimeAction <= now) {
          long size = calculateSize(newToSend.toSend);
          trafficCounter.bytesRealWriteFlowControl(size);
          queueSize -= size;
          ctx.write(newToSend.toSend, newToSend.promise);
        } else {
          messagesQueue.addFirst(newToSend);
          break;
        }
      }
      if (messagesQueue.isEmpty()) {
        releaseWriteSuspended(ctx);
      }
    }
    ctx.flush();
  }*/
}
