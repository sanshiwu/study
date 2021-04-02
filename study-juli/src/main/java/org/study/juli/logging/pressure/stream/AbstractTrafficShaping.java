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

  protected void setTrafficCounter(TrafficCounter newTrafficCounter) {
    trafficCounter = newTrafficCounter;
  }

  protected int userDefinedWritabilityIndex() {
    return CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX;
  }
}
