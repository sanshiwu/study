package org.study.juli.logging.pressure.stream;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
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
 * @version 2021-04-07 19:05
 * @since 2021-04-07 19:05:00
 */
public class GlobalFileTrafficShaping extends AbstractTrafficShaping {
  private final ConcurrentMap<Integer, PerChannel> channelQueues = new ConcurrentHashMap<>();

  /** Global queues size */
  private final AtomicLong queuesSize = new AtomicLong();

  /**
   * Maximum cumulative writing bytes for one channel among all (as long as channels stay the same)
   */
  private final AtomicLong cumulativeWrittenBytes = new AtomicLong();

  /** Maximum cumulative read bytes for one channel among all (as long as channels stay the same) */
  private final AtomicLong cumulativeReadBytes = new AtomicLong();

  /**
   * Max size in the list before proposing to stop writing new objects from next handlers for all
   * channel (global)
   */
  volatile long maxGlobalWriteSize = DEFAULT_MAX_SIZE * 100; // default 400MB

  static final class PerChannel {
    ArrayDeque<ToSend> messagesQueue;
    TrafficCounter channelTrafficCounter;
    long queueSize;
    long lastWriteTimestamp;
    long lastReadTimestamp;
  }
  /** Limit in B/s to apply to write */
  private volatile long writeChannelLimit;

  /** Limit in B/s to apply to read */
  private volatile long readChannelLimit;

  private static final float DEFAULT_DEVIATION = 0.1F;
  private static final float MAX_DEVIATION = 0.4F;
  private static final float DEFAULT_SLOWDOWN = 0.4F;
  private static final float DEFAULT_ACCELERATION = -0.1F;
  private volatile float maxDeviation;
  private volatile float accelerationFactor;
  private volatile float slowDownFactor;
  private volatile boolean readDeviationActive;
  private volatile boolean writeDeviationActive;

  protected GlobalFileTrafficShaping(
      long writeLimit, long readLimit, long checkInterval, long maxTime) {
    super(writeLimit, readLimit, checkInterval, maxTime);
  }

  @Override
  void submitWrite(Object msg, long size, long delay, long now) {}

  /** @return the current max deviation */
  public float maxDeviation() {
    return maxDeviation;
  }

  /** @return the current acceleration factor */
  public float accelerationFactor() {
    return accelerationFactor;
  }

  /** @return the current slow down factor */
  public float slowDownFactor() {
    return slowDownFactor;
  }

  /**
   * @param maxDeviation the maximum deviation to allow during computation of average, default
   *     deviation being 0.1, so +/-10% of the desired bandwidth. Maximum being 0.4.
   * @param slowDownFactor the factor set as +x% to the too fast client (minimal value being 0,
   *     meaning no slow down factor), default being 40% (0.4).
   * @param accelerationFactor the factor set as -x% to the too slow client (maximal value being 0,
   *     meaning no acceleration factor), default being -10% (-0.1).
   */
  public void setMaxDeviation(float maxDeviation, float slowDownFactor, float accelerationFactor) {
    if (maxDeviation > MAX_DEVIATION) {
      throw new IllegalArgumentException("maxDeviation must be <= " + MAX_DEVIATION);
    }
    if (slowDownFactor < 0) {
      throw new IllegalArgumentException("slowDownFactor must be >= 0");
    }
    if (accelerationFactor > 0) {
      throw new IllegalArgumentException("accelerationFactor must be <= 0");
    }
    this.maxDeviation = maxDeviation;
    this.accelerationFactor = 1 + accelerationFactor;
    this.slowDownFactor = 1 + slowDownFactor;
  }

  private void computeDeviationCumulativeBytes() {
    // compute the maximum cumulativeXxxxBytes among still connected Channels
    long maxWrittenBytes = 0;
    long maxReadBytes = 0;
    long minWrittenBytes = Long.MAX_VALUE;
    long minReadBytes = Long.MAX_VALUE;
    for (PerChannel perChannel : channelQueues.values()) {
      long value = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
      if (maxWrittenBytes < value) {
        maxWrittenBytes = value;
      }
      if (minWrittenBytes > value) {
        minWrittenBytes = value;
      }
      value = perChannel.channelTrafficCounter.cumulativeReadBytes();
      if (maxReadBytes < value) {
        maxReadBytes = value;
      }
      if (minReadBytes > value) {
        minReadBytes = value;
      }
    }
    boolean multiple = channelQueues.size() > 1;
    readDeviationActive = multiple && minReadBytes < maxReadBytes / 2;
    writeDeviationActive = multiple && minWrittenBytes < maxWrittenBytes / 2;
    cumulativeWrittenBytes.set(maxWrittenBytes);
    cumulativeReadBytes.set(maxReadBytes);
  }

  @Override
  protected void doAccounting(TrafficCounter counter) {
    computeDeviationCumulativeBytes();
    super.doAccounting(counter);
  }

  private long computeBalancedWait(float maxLocal, float maxGlobal, long wait) {
    if (maxGlobal == 0) {
      // no change
      return wait;
    }
    float ratio = maxLocal / maxGlobal;
    // if in the boundaries, same value
    if (ratio > maxDeviation) {
      if (ratio < 1 - maxDeviation) {
        return wait;
      } else {
        ratio = slowDownFactor;
        if (wait < MINIMAL_WAIT) {
          wait = MINIMAL_WAIT;
        }
      }
    } else {
      ratio = accelerationFactor;
    }
    return (long) (wait * ratio);
  }

  /** @return the maxGlobalWriteSize */
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
   *     channels before write suspended is set.
   */
  public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
    if (maxGlobalWriteSize <= 0) {
      throw new IllegalArgumentException("maxGlobalWriteSize must be positive");
    }
    this.maxGlobalWriteSize = maxGlobalWriteSize;
  }

  /** @return the global size of the buffers for all queues. */
  public long queuesSize() {
    return queuesSize.get();
  }

  /**
   * @param newWriteLimit Channel write limit
   * @param newReadLimit Channel read limit
   */
  public void configureChannel(long newWriteLimit, long newReadLimit) {
    writeChannelLimit = newWriteLimit;
    readChannelLimit = newReadLimit;
    long now = TrafficCounter.milliSecondFromNano();
    for (PerChannel perChannel : channelQueues.values()) {
      perChannel.channelTrafficCounter.resetAccounting(now);
    }
  }

  /** @return Channel write limit */
  public long getWriteChannelLimit() {
    return writeChannelLimit;
  }

  /** @param writeLimit Channel write limit */
  public void setWriteChannelLimit(long writeLimit) {
    writeChannelLimit = writeLimit;
    long now = TrafficCounter.milliSecondFromNano();
    for (PerChannel perChannel : channelQueues.values()) {
      perChannel.channelTrafficCounter.resetAccounting(now);
    }
  }

  /** @return Channel read limit */
  public long getReadChannelLimit() {
    return readChannelLimit;
  }

  /** @param readLimit Channel read limit */
  public void setReadChannelLimit(long readLimit) {
    readChannelLimit = readLimit;
    long now = TrafficCounter.milliSecondFromNano();
    for (PerChannel perChannel : channelQueues.values()) {
      perChannel.channelTrafficCounter.resetAccounting(now);
    }
  }

  /** Release all internal resources of this instance. */
  public final void release() {
    trafficCounter.stop();
  }

  private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
    PerChannel perChannel = channelQueues.get("");
    if (perChannel == null) {
      perChannel = new PerChannel();
      perChannel.messagesQueue = new ArrayDeque<ToSend>();
      // Don't start it since managed through the Global one
      perChannel.channelTrafficCounter =
          new TrafficCounter(this, null, "ChannelTC", checkInterval);
      perChannel.queueSize = 0L;
      perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
      perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
      channelQueues.put(1, perChannel);
    }
    return perChannel;
  }

  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    getOrSetPerChannel(ctx);
    trafficCounter.resetCumulativeTime();
  }

  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    trafficCounter.resetCumulativeTime();
    PerChannel perChannel = channelQueues.remove("");
    if (perChannel != null) {
      // write operations need synchronization
      synchronized (perChannel) {
        for (ToSend toSend : perChannel.messagesQueue) {
          long size = calculateSize(toSend.toSend);
          trafficCounter.bytesRealWriteFlowControl(size);
          perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
          perChannel.queueSize -= size;
          queuesSize.addAndGet(-size);
          perChannel.messagesQueue.clear();
        }
      }
      releaseWriteSuspended();
      releaseReadSuspended(ctx);
    }
  }

  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    long size = calculateSize(msg);
    long now = TrafficCounter.milliSecondFromNano();
    if (size > 0) {
      // compute the number of ms to wait before reopening the channel
      long waitGlobal = trafficCounter.readTimeToWait(size, getReadLimit(), maxTime, now);
      Integer key = 0;
      PerChannel perChannel = channelQueues.get(key);
      long wait = 0;
      if (perChannel != null) {
        wait =
            perChannel.channelTrafficCounter.readTimeToWait(size, readChannelLimit, maxTime, now);
        if (readDeviationActive) {
          // now try to balance between the channels
          long maxLocalRead;
          maxLocalRead = perChannel.channelTrafficCounter.cumulativeReadBytes();
          long maxGlobalRead = cumulativeReadBytes.get();
          if (maxLocalRead <= 0) {
            maxLocalRead = 0;
          }
          if (maxGlobalRead < maxLocalRead) {
            maxGlobalRead = maxLocalRead;
          }
          wait = computeBalancedWait(maxLocalRead, maxGlobalRead, wait);
        }
      }
      if (wait < waitGlobal) {
        wait = waitGlobal;
      }
      wait = checkWaitReadTime(ctx, wait, now);
      if (wait >= MINIMAL_WAIT) { // At least 10ms seems a minimal
        // time in order to try to limit the traffic
        // Only AutoRead AND HandlerActive True means Context Active
      }
    }
    informReadOperation(ctx, now);
  }

  protected long checkWaitReadTime(final ChannelHandlerContext ctx, long wait, final long now) {
    Integer key = 0;
    PerChannel perChannel = channelQueues.get(key);
    if (perChannel != null) {
      if (wait > maxTime && now + wait - perChannel.lastReadTimestamp > maxTime) {
        wait = maxTime;
      }
    }
    return wait;
  }

  protected void informReadOperation(final ChannelHandlerContext ctx, final long now) {
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

    private ToSend(final long delay, final Object toSend, final long size) {
      relativeTimeAction = delay;
      this.toSend = toSend;
      this.size = size;
    }
  }

  protected long maximumCumulativeWrittenBytes() {
    return cumulativeWrittenBytes.get();
  }

  protected long maximumCumulativeReadBytes() {
    return cumulativeReadBytes.get();
  }

  /**
   * To allow for instance doAccounting to use the TrafficCounter per channel.
   *
   * @return the list of TrafficCounters that exists at the time of the call.
   */
  public Collection<TrafficCounter> channelTrafficCounters() {
    return new AbstractCollection<TrafficCounter>() {
      @Override
      public Iterator<TrafficCounter> iterator() {
        return new Iterator<TrafficCounter>() {
          final Iterator<PerChannel> iter = channelQueues.values().iterator();

          @Override
          public boolean hasNext() {
            return iter.hasNext();
          }

          @Override
          public TrafficCounter next() {
            return iter.next().channelTrafficCounter;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      public int size() {
        return channelQueues.size();
      }
    };
  }

  public void write(final ChannelHandlerContext ctx, final Object msg)
      throws Exception {
    long size = calculateSize(msg);
    long now = TrafficCounter.milliSecondFromNano();
    if (size > 0) {
      // compute the number of ms to wait before continue with the channel
      long waitGlobal = trafficCounter.writeTimeToWait(size, getWriteLimit(), maxTime, now);
      Integer key = 0;
      PerChannel perChannel = channelQueues.get(key);
      long wait = 0;
      if (perChannel != null) {
        wait =
            perChannel.channelTrafficCounter.writeTimeToWait(size, writeChannelLimit, maxTime, now);
        if (writeDeviationActive) {
          // now try to balance between the channels
          long maxLocalWrite;
          maxLocalWrite = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
          long maxGlobalWrite = cumulativeWrittenBytes.get();
          if (maxLocalWrite <= 0) {
            maxLocalWrite = 0;
          }
          if (maxGlobalWrite < maxLocalWrite) {
            maxGlobalWrite = maxLocalWrite;
          }
          wait = computeBalancedWait(maxLocalWrite, maxGlobalWrite, wait);
        }
      }
      if (wait < waitGlobal) {
        wait = waitGlobal;
      }
      if (wait >= MINIMAL_WAIT) {

        submitWrite(ctx, msg, size, wait, now);
        return;
      }
    }
    // to maintain order of write
    submitWrite(ctx, msg, size, 0, now);
  }

  protected void submitWrite(
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
        perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
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
      checkWriteSuspend(delay, perChannel.queueSize);
      if (queuesSize.get() > maxGlobalWriteSize) {
        globalSizeExceeded = true;
      }
    }
    if (globalSizeExceeded) {
      setUserDefinedWritability(false);
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
          perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
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

  @Override
  public String toString() {
    return new StringBuilder(340)
        .append(super.toString())
        .append(" Write Channel Limit: ")
        .append(writeChannelLimit)
        .append(" Read Channel Limit: ")
        .append(readChannelLimit)
        .toString();
  }
}
