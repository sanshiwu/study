package org.study.juli.logging.pressure.stream;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-07 19:07
 * @since 2021-04-07 19:07:00
 */
public class GlobalTrafficShaping extends AbstractTrafficShaping {
  private final ConcurrentMap<Integer, PerChannel> channelQueues = new ConcurrentHashMap<>();

  protected GlobalTrafficShaping(
      long writeLimit, long readLimit, long checkInterval, long maxTime) {
    super(writeLimit, readLimit, checkInterval, maxTime);
  }

  @Override
  void submitWrite(Object msg, long size, long delay, long now) {}

  /** Global queues size */
  private final AtomicLong queuesSize = new AtomicLong();

  /**
   * Max size in the list before proposing to stop writing new objects from next handlers for all
   * channel (global)
   */
  long maxGlobalWriteSize = DEFAULT_MAX_SIZE * 100; // default 400MB

  private static final class PerChannel {
    ArrayDeque<ToSend> messagesQueue;
    long queueSize;
    long lastWriteTimestamp;
    long lastReadTimestamp;
  }

  /** @return the maxGlobalWriteSize default value being 400 MB. */
  public long getMaxGlobalWriteSize() {
    return maxGlobalWriteSize;
  }

  /**
   * Note the change will be taken as best effort, meaning that all already scheduled traffics will
   * not be changed, but only applied to new traffics.<br>
   * So the expected usage of this method is to be used not too often, accordingly to the traffic
   * shaping configuration.
   *
   * @param maxGlobalWriteSize the maximum Global Write Size allowed in the buffer globally for all
   *     channels before write suspended is set, default value being 400 MB.
   */
  public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
    this.maxGlobalWriteSize = maxGlobalWriteSize;
  }

  /** @return the global size of the buffers for all queues. */
  public long queuesSize() {
    return queuesSize.get();
  }

  /** Release all internal resources of this instance. */
  public final void release() {
    trafficCounter.stop();
  }

  private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
    Integer key = 0;
    PerChannel perChannel = channelQueues.get(key);
    if (perChannel == null) {
      perChannel = new PerChannel();
      perChannel.messagesQueue = new ArrayDeque<ToSend>();
      perChannel.queueSize = 0L;
      perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
      perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
      channelQueues.put(key, perChannel);
    }
    return perChannel;
  }

  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    getOrSetPerChannel(ctx);
  }

  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    PerChannel perChannel = channelQueues.remove("");
    if (perChannel != null) {
      // write operations need synchronization
      synchronized (perChannel) {

          for (ToSend toSend : perChannel.messagesQueue) {
            long size = calculateSize(toSend.toSend);
            trafficCounter.bytesRealWriteFlowControl(size);
            perChannel.queueSize -= size;
            queuesSize.addAndGet(-size);
          }
        perChannel.messagesQueue.clear();
      }
    }
    releaseWriteSuspended();
    releaseReadSuspended(ctx);
  }

  long checkWaitReadTime(final ChannelHandlerContext ctx, long wait, final long now) {
    Integer key = 0;
    PerChannel perChannel = channelQueues.get(key);
    if (perChannel != null) {
      if (wait > maxTime && now + wait - perChannel.lastReadTimestamp > maxTime) {
        wait = maxTime;
      }
    }
    return wait;
  }

  void informReadOperation(final ChannelHandlerContext ctx, final long now) {
    Integer key = 0;
    PerChannel perChannel = channelQueues.get(key);
    if (perChannel != null) {
      perChannel.lastReadTimestamp = now;
    }
  }

  private static final class ToSend {
    final long relativeTimeAction;
    final Object toSend;
    final long size;

    private ToSend(
        final long delay, final Object toSend, final long size) {
      relativeTimeAction = delay;
      this.toSend = toSend;
      this.size = size;
    }
  }

  void submitWrite(
      final ChannelHandlerContext ctx,
      final Object msg,
      final long size,
      final long writedelay,
      final long now) {
    Integer key = 0;
    PerChannel perChannel = channelQueues.get(key);
    if (perChannel == null) {
      // in case write occurs before handlerAdded is raised for this handler
      // imply a synchronized only if needed
      perChannel = getOrSetPerChannel(ctx);
    }
    final ToSend newToSend;
    long delay = writedelay;
    boolean globalSizeExceeded = false;
    // write operations need synchronization
    synchronized (perChannel) {
      if (writedelay == 0 && perChannel.messagesQueue.isEmpty()) {
        trafficCounter.bytesRealWriteFlowControl(size);
        perChannel.lastWriteTimestamp = now;
        return;
      }
      if (delay > maxTime && now + delay - perChannel.lastWriteTimestamp > maxTime) {
        delay = maxTime;
      }
      newToSend = new ToSend(delay + now, msg, size);
      perChannel.messagesQueue.addLast(newToSend);
      perChannel.queueSize += size;
      queuesSize.addAndGet(size);
      checkWriteSuspend( delay, perChannel.queueSize);
      if (queuesSize.get() > maxGlobalWriteSize) {
        globalSizeExceeded = true;
      }
    }
    if (globalSizeExceeded) {
      setUserDefinedWritability( false);
    }
    final long futureNow = newToSend.relativeTimeAction;
    final PerChannel forSchedule = perChannel;
  }

  private void sendAllValid(
      final ChannelHandlerContext ctx, final PerChannel perChannel, final long now) {
    // write operations need synchronization
    synchronized (perChannel) {
      ToSend newToSend = perChannel.messagesQueue.pollFirst();
      for (; newToSend != null; newToSend = perChannel.messagesQueue.pollFirst()) {
        if (newToSend.relativeTimeAction <= now) {
          long size = newToSend.size;
          trafficCounter.bytesRealWriteFlowControl(size);
          perChannel.queueSize -= size;
          queuesSize.addAndGet(-size);
          perChannel.lastWriteTimestamp = now;
        } else {
          perChannel.messagesQueue.addFirst(newToSend);
          break;
        }
      }
      if (perChannel.messagesQueue.isEmpty()) {
        releaseWriteSuspended();
      }
    }
  }
}
