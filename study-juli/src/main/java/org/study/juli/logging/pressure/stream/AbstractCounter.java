package org.study.juli.logging.pressure.stream;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-02 15:55
 * @since 2021-04-02 15:55:00
 */
public abstract class AbstractCounter implements Counter {
  protected long lastCumulativeTime;
  protected long lastWriteThroughput;
  protected long lastReadThroughput;
  protected final AtomicLong lastTime = new AtomicLong();
  protected long lastWrittenBytes;
  protected long lastReadBytes;
  protected long lastWritingTime;
  protected long lastReadingTime;
  protected final AtomicLong realWrittenBytes = new AtomicLong();
  protected long realWriteThroughput;
  protected final AtomicLong checkInterval =
      new AtomicLong(AbstractTrafficShaping.DEFAULT_CHECK_INTERVAL);
  protected String name;
  protected AbstractTrafficShaping trafficShapingHandler;
  protected ScheduledExecutorService executor;
  protected Runnable monitor;
  protected ScheduledFuture<?> scheduledFuture;
  protected boolean monitorActive;
}
