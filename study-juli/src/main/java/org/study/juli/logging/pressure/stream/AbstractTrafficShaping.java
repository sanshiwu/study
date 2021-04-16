package org.study.juli.logging.pressure.stream;

import org.study.juli.logging.base.Constants;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-03-24 21:57
 * @since 2021-03-24 21:57:00
 */
public abstract class AbstractTrafficShaping implements TrafficShaping {
  public static final long DEFAULT_CHECK_INTERVAL = 1000;
  public static final long DEFAULT_MAX_TIME = 15000;
  protected static final long DEFAULT_MAX_SIZE = 4 * 1024 * 1024L;
  protected static final long MINIMAL_WAIT = 10;
  protected static final int USER_DEFINED_WRIT_ABILITY_INDEX = 0;
  protected static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
  protected static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
  protected static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;
  protected static final boolean READ_SUSPENDED = false;
  protected static final Runnable REOPEN_TASK = null;
  protected TrafficCounter trafficCounter;
  private long writeLimit;
  private long readLimit;
  protected long maxTime = DEFAULT_MAX_TIME;
  protected long checkInterval = DEFAULT_CHECK_INTERVAL;
  protected long maxWriteDelay = Constants.LOOP_COUNT * DEFAULT_CHECK_INTERVAL;
  protected long maxWriteSize = DEFAULT_MAX_SIZE;

  protected final int userDefinedWritabilityIndex;

  protected void setTrafficCounter(TrafficCounter newTrafficCounter) {
    trafficCounter = newTrafficCounter;
  }

  protected int userDefinedWritabilityIndex() {
    return CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX;
  }

  protected AbstractTrafficShaping(
      long writeLimit, long readLimit, long checkInterval, long maxTime) {
    userDefinedWritabilityIndex = userDefinedWritabilityIndex();
    this.writeLimit = writeLimit;
    this.readLimit = readLimit;
    this.checkInterval = checkInterval;
    this.maxTime = maxTime;
  }

  public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
    configure(newWriteLimit, newReadLimit);
    configure(newCheckInterval);
  }

  public void configure(long newWriteLimit, long newReadLimit) {
    writeLimit = newWriteLimit;
    readLimit = newReadLimit;
    if (trafficCounter != null) {
      trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
    }
  }

  public void configure(long newCheckInterval) {
    checkInterval = newCheckInterval;
    if (trafficCounter != null) {
      trafficCounter.configure(checkInterval);
    }
  }

  public long getWriteLimit() {
    return writeLimit;
  }

  public void setWriteLimit(long writeLimit) {
    this.writeLimit = writeLimit;
    if (trafficCounter != null) {
      trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
    }
  }

  public long getReadLimit() {
    return readLimit;
  }

  public void setReadLimit(long readLimit) {
    this.readLimit = readLimit;
    if (trafficCounter != null) {
      trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
    }
  }

  public long getCheckInterval() {
    return checkInterval;
  }

  public void setCheckInterval(long checkInterval) {
    this.checkInterval = checkInterval;
    if (trafficCounter != null) {
      trafficCounter.configure(checkInterval);
    }
  }

  public void setMaxTimeWait(long maxTime) {
    if (maxTime <= 0) {
      throw new IllegalArgumentException("maxTime must be positive");
    }
    this.maxTime = maxTime;
  }

  public long getMaxTimeWait() {
    return maxTime;
  }

  public long getMaxWriteDelay() {
    return maxWriteDelay;
  }

  public void setMaxWriteDelay(long maxWriteDelay) {
    if (maxWriteDelay <= 0) {
      throw new IllegalArgumentException("maxWriteDelay must be positive");
    }
    this.maxWriteDelay = maxWriteDelay;
  }

  public long getMaxWriteSize() {
    return maxWriteSize;
  }

  public void setMaxWriteSize(long maxWriteSize) {
    this.maxWriteSize = maxWriteSize;
  }

  protected void doAccounting(TrafficCounter counter) {
    // NOOP by default
  }

  public void channelRead(final Object msg) throws Exception {
    long size = calculateSize(msg);
    long now = TrafficCounter.milliSecondFromNano();
    if (size > 0) {
      // compute the number of ms to wait before reopening the channel
      long wait = trafficCounter.readTimeToWait(size, readLimit, maxTime, now);
      wait = checkWaitReadTime(wait, now);
      if (wait >= MINIMAL_WAIT) {
        // At least 10ms seems a minimal
        // time in order to try to limit the traffic
        // Only AutoRead AND HandlerActive True means Context Active

      }
    }
    informReadOperation(now);
  }

  long checkWaitReadTime(long wait, final long now) {
    // no change by default
    return wait;
  }

  void informReadOperation(final long now) {
    // default noop
  }

  public void write(final Object msg) throws Exception {
    long size = calculateSize(msg);
    long now = TrafficCounter.milliSecondFromNano();
    if (size > 0) {
      // compute the number of ms to wait before continue with the channel
      long wait = trafficCounter.writeTimeToWait(size, writeLimit, maxTime, now);
      if (wait >= MINIMAL_WAIT) {
        submitWrite(msg, size, wait, now);
        return;
      }
    }
    // to maintain order of write
    submitWrite(msg, size, 0, now);
  }

  abstract void submitWrite(Object msg, long size, long delay, long now);

  void setUserDefinedWritability(boolean writable) {
    ChannelOutboundBuffer outboundBuffer = new ChannelOutboundBuffer();
    outboundBuffer.setUserDefinedWritability(userDefinedWritabilityIndex, writable);
  }

  void checkWriteSuspend(long delay, long queueSize) {
    if (queueSize > maxWriteSize || delay > maxWriteDelay) {
      setUserDefinedWritability(false);
    }
  }

  void releaseWriteSuspended() {
    setUserDefinedWritability(true);
  }

  void releaseReadSuspended(ChannelHandlerContext ctx) {}

  public TrafficCounter trafficCounter() {
    return trafficCounter;
  }

  @Override
  public String toString() {
    StringBuilder builder =
        new StringBuilder(290)
            .append("TrafficShaping with Write Limit: ")
            .append(writeLimit)
            .append(" Read Limit: ")
            .append(readLimit)
            .append(" CheckInterval: ")
            .append(checkInterval)
            .append(" maxDelay: ")
            .append(maxWriteDelay)
            .append(" maxSize: ")
            .append(maxWriteSize)
            .append(" and Counter: ");
    if (trafficCounter != null) {
      builder.append(trafficCounter);
    } else {
      builder.append("none");
    }
    return builder.toString();
  }

  protected long calculateSize(Object msg) {
    return -1;
  }
}
