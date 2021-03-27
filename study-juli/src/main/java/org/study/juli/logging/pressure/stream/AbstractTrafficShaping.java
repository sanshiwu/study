package org.study.juli.logging.pressure.stream;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-03-24 21:57
 * @since 2021-03-24 21:57:00
 */
public class AbstractTrafficShaping {
  /** Default delay between two checks: 1s */
  public static final long DEFAULT_CHECK_INTERVAL = 1000;

  /**
   * Default max delay in case of traffic shaping (during which no communication will occur). Shall
   * be less than TIMEOUT. Here half of "standard" 30s
   */
  public static final long DEFAULT_MAX_TIME = 15000;

  /** Default max size to not exceed in buffer (write only). */
  static final long DEFAULT_MAX_SIZE = 4 * 1024 * 1024L;

  /** Default minimal time to wait: 10ms */
  static final long MINIMAL_WAIT = 10;

  /** Traffic Counter */
  protected TrafficCounter trafficCounter;

  /** Limit in B/s to apply to write */
  private volatile long writeLimit;

  /** Limit in B/s to apply to read */
  private volatile long readLimit;

  /** Max delay in wait */
  protected volatile long maxTime = DEFAULT_MAX_TIME; // default 15 s

  /** Delay between two performance snapshots */
  protected volatile long checkInterval = DEFAULT_CHECK_INTERVAL; // default 1 s

  static final boolean READ_SUSPENDED = false;
  static final Runnable REOPEN_TASK = null;

  /** Max time to delay before proposing to stop writing new objects from next handlers */
  volatile long maxWriteDelay = 4 * DEFAULT_CHECK_INTERVAL; // default 4 s
  /** Max size in the list before proposing to stop writing new objects from next handlers */
  volatile long maxWriteSize = DEFAULT_MAX_SIZE; // default 4MB

  /**
   * Rank in UserDefinedWritability (1 for Channel, 2 for Global TrafficShapingHandler). Set in
   * final constructor. Must be between 1 and 31
   */
  final int userDefinedWritabilityIndex = 0;

  /** Default value for Channel UserDefinedWritability index */
  static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;

  /** Default value for Global UserDefinedWritability index */
  static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;

  /** Default value for GlobalChannel UserDefinedWritability index */
  static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;

  void setTrafficCounter(TrafficCounter newTrafficCounter) {
    trafficCounter = newTrafficCounter;
  }

  protected int userDefinedWritabilityIndex() {
    return CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX;
  }
}
