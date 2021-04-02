package org.study.juli.logging.pressure.stream;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.study.juli.logging.base.Constants;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-03-24 21:58
 * @since 2021-03-24 21:58:00
 */
public class TrafficCounter extends AbstractCounter {
  private final AtomicLong currentWrittenBytes = new AtomicLong();
  private final AtomicLong currentReadBytes = new AtomicLong();
  private long writingTime;
  private long readingTime;
  private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
  private final AtomicLong cumulativeReadBytes = new AtomicLong();

  public TrafficCounter(ScheduledExecutorService executor, String name, long checkInterval) {

    this.name = name;
    trafficShapingHandler = null;
    this.executor = executor;

    init(checkInterval);
  }

  public TrafficCounter(
      AbstractTrafficShaping trafficShapingHandler,
      ScheduledExecutorService executor,
      String name,
      long checkInterval) {

    if (trafficShapingHandler == null) {
      throw new IllegalArgumentException("trafficShapingHandler");
    }

    this.name = name;
    this.trafficShapingHandler = trafficShapingHandler;
    this.executor = executor;

    init(checkInterval);
  }

  public static long milliSecondFromNano() {
    return System.nanoTime() / Constants.CAPACITY;
  }

  private final class TrafficMonitoringTask implements Runnable {
    @Override
    public void run() {
      if (!monitorActive) {
        return;
      }
      resetAccounting(milliSecondFromNano());
      if (trafficShapingHandler != null) {
        //
      }
    }
  }

  public synchronized void start() {
    if (monitorActive) {
      return;
    }
    lastTime.set(milliSecondFromNano());
    long localCheckInterval = checkInterval.get();
    // if executor is null, it means it is piloted by a GlobalChannelTrafficCounter, so no executor
    if (localCheckInterval > 0 && executor != null) {
      monitorActive = true;
      monitor = new TrafficMonitoringTask();
      scheduledFuture =
          executor.scheduleAtFixedRate(monitor, 0, localCheckInterval, TimeUnit.MILLISECONDS);
    }
  }

  public synchronized void stop() {
    if (!monitorActive) {
      return;
    }
    monitorActive = false;
    resetAccounting(milliSecondFromNano());
    if (trafficShapingHandler != null) {
      //
    }
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
  }

  synchronized void resetAccounting(long newLastTime) {
    long interval = newLastTime - lastTime.getAndSet(newLastTime);
    if (interval == 0) {
      // nothing to do
      return;
    }
    lastReadBytes = currentReadBytes.getAndSet(0);
    lastWrittenBytes = currentWrittenBytes.getAndSet(0);
    lastReadThroughput = lastReadBytes * 1000 / interval;
    // nb byte / checkInterval in ms * 1000 (1s)
    lastWriteThroughput = lastWrittenBytes * 1000 / interval;
    // nb byte / checkInterval in ms * 1000 (1s)
    realWriteThroughput = realWrittenBytes.getAndSet(0) * 1000 / interval;
    lastWritingTime = Math.max(lastWritingTime, writingTime);
    lastReadingTime = Math.max(lastReadingTime, readingTime);
  }

  private void init(long checkInterval) {
    // absolute time: informative only
    lastCumulativeTime = System.currentTimeMillis();
    writingTime = milliSecondFromNano();
    readingTime = writingTime;
    lastWritingTime = writingTime;
    lastReadingTime = writingTime;
    configure(checkInterval);
  }

  public void configure(long newCheckInterval) {
    long newInterval = newCheckInterval / 10 * 10;
    if (checkInterval.getAndSet(newInterval) != newInterval) {
      if (newInterval <= 0) {
        stop();
        // No more active monitoring
        lastTime.set(milliSecondFromNano());
      } else {
        // Restart
        stop();
        start();
      }
    }
  }

  void bytesRecvFlowControl(long recv) {
    currentReadBytes.addAndGet(recv);
    cumulativeReadBytes.addAndGet(recv);
  }

  void bytesWriteFlowControl(long write) {
    currentWrittenBytes.addAndGet(write);
    cumulativeWrittenBytes.addAndGet(write);
  }

  void bytesRealWriteFlowControl(long write) {
    realWrittenBytes.addAndGet(write);
  }

  public long checkInterval() {
    return checkInterval.get();
  }

  public long lastReadThroughput() {
    return lastReadThroughput;
  }

  public long lastWriteThroughput() {
    return lastWriteThroughput;
  }

  public long lastReadBytes() {
    return lastReadBytes;
  }

  public long lastWrittenBytes() {
    return lastWrittenBytes;
  }

  public long currentReadBytes() {
    return currentReadBytes.get();
  }

  public long currentWrittenBytes() {
    return currentWrittenBytes.get();
  }

  public long lastTime() {
    return lastTime.get();
  }

  public long cumulativeWrittenBytes() {
    return cumulativeWrittenBytes.get();
  }

  public long cumulativeReadBytes() {
    return cumulativeReadBytes.get();
  }

  public long lastCumulativeTime() {
    return lastCumulativeTime;
  }

  public AtomicLong getRealWrittenBytes() {
    return realWrittenBytes;
  }

  public long getRealWriteThroughput() {
    return realWriteThroughput;
  }

  public void resetCumulativeTime() {
    lastCumulativeTime = System.currentTimeMillis();
    cumulativeReadBytes.set(0);
    cumulativeWrittenBytes.set(0);
  }

  public String name() {
    return name;
  }

  public long readTimeToWait(final long size, final long limitTraffic, final long maxTime) {
    return readTimeToWait(size, limitTraffic, maxTime, milliSecondFromNano());
  }

  public long readTimeToWait(
      final long size, final long limitTraffic, final long maxTime, final long now) {
    bytesRecvFlowControl(size);
    if (size == 0 || limitTraffic == 0) {
      return 0;
    }
    final long lastTimeCheck = lastTime.get();
    long sum = currentReadBytes.get();
    long localReadingTime = readingTime;

    final long interval = now - lastTimeCheck;
    long pastDelay = Math.max(lastReadingTime - lastTimeCheck, 0);
    if (interval > AbstractTrafficShaping.MINIMAL_WAIT) {
      // Enough interval time to compute shaping
      long time = sum * 1000 / limitTraffic - interval + pastDelay;
      if (time > AbstractTrafficShaping.MINIMAL_WAIT) {
        if (time > maxTime && now + time - localReadingTime > maxTime) {
          time = maxTime;
        }
        readingTime = Math.max(localReadingTime, now + time);
        return time;
      }
      readingTime = Math.max(localReadingTime, now);
      return 0;
    }
    long lastRb = lastReadBytes;
    // take the last read interval check to get enough interval time
    long lastsum = sum + lastRb;
    long lastinterval = interval + checkInterval.get();
    long time = lastsum * 1000 / limitTraffic - lastinterval + pastDelay;
    if (time > AbstractTrafficShaping.MINIMAL_WAIT) {
      if (time > maxTime && now + time - localReadingTime > maxTime) {
        time = maxTime;
      }
      readingTime = Math.max(localReadingTime, now + time);
      return time;
    }
    readingTime = Math.max(localReadingTime, now);
    return 0;
  }

  public long writeTimeToWait(final long size, final long limitTraffic, final long maxTime) {
    return writeTimeToWait(size, limitTraffic, maxTime, milliSecondFromNano());
  }

  public long writeTimeToWait(
      final long size, final long limitTraffic, final long maxTime, final long now) {
    bytesWriteFlowControl(size);
    if (size == 0 || limitTraffic == 0) {
      return 0;
    }
    final long lastTimeCheck = lastTime.get();
    long sum = currentWrittenBytes.get();

    long localWritingTime = writingTime;
    long pastDelay = Math.max(lastWritingTime - lastTimeCheck, 0);
    final long interval = now - lastTimeCheck;
    if (interval > AbstractTrafficShaping.MINIMAL_WAIT) {
      // Enough interval time to compute shaping
      long time = sum * 1000 / limitTraffic - interval + pastDelay;
      if (time > AbstractTrafficShaping.MINIMAL_WAIT) {
        if (time > maxTime && now + time - localWritingTime > maxTime) {
          time = maxTime;
        }
        writingTime = Math.max(localWritingTime, now + time);
        return time;
      }
      writingTime = Math.max(localWritingTime, now);
      return 0;
    }
    long lastWb = lastWrittenBytes;
    // take the last write interval check to get enough interval time
    long lastsum = sum + lastWb;
    long lastinterval = interval + checkInterval.get();
    long time = lastsum * 1000 / limitTraffic - lastinterval + pastDelay;
    if (time > AbstractTrafficShaping.MINIMAL_WAIT) {
      if (time > maxTime && now + time - localWritingTime > maxTime) {
        time = maxTime;
      }
      writingTime = Math.max(localWritingTime, now + time);
      return time;
    }
    writingTime = Math.max(localWritingTime, now);
    return 0;
  }

  @Override
  public String toString() {
    String kb = " KB/s, ";
    return new StringBuilder(165)
        .append("Monitor ")
        .append(name)
        .append(" Current Speed Read: ")
        .append(lastReadThroughput >> 10)
        .append(kb)
        .append("Asked Write: ")
        .append(lastWriteThroughput >> 10)
        .append(kb)
        .append("Real Write: ")
        .append(realWriteThroughput >> 10)
        .append(kb)
        .append("Current Read: ")
        .append(currentReadBytes.get() >> 10)
        .append(" KB, ")
        .append("Current asked Write: ")
        .append(currentWrittenBytes.get() >> 10)
        .append(" KB, ")
        .append("Current real Write: ")
        .append(realWrittenBytes.get() >> 10)
        .append(" KB")
        .toString();
  }
}
