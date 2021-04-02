package org.study.juli.logging.pressure.water;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public final class FileInboundBuffer extends AbstractBoundBuffer {
  /** 未来可以考虑使用AtomicLongFieldUpdater. */
  private final AtomicLong count = new AtomicLong(0L);
  /** 未来可以考虑使用AtomicLongFieldUpdater. */
  private final AtomicLong size = new AtomicLong(0L);
  /** 控制写水位的锁. */
  private final ReentrantLock lock = new ReentrantLock();
  /** Condition for waiting takes */
  private final Condition countLimitLockCondition = lock.newCondition();

  private final AtomicBoolean isWritable = new AtomicBoolean(true);
  private final AtomicBoolean isReadable = new AtomicBoolean(true);

  public FileInboundBuffer() {
    //
  }

  public synchronized boolean isWritable() {
    return isWritable.get();
  }

  public AtomicLong getCount() {
    return count;
  }

  public boolean checkHigh() {
    int countHigh = COUNT_WATER_MARK.high();
    long l = count.get();
    if (l >= countHigh) {
      isWritable.set(false);
      return false;
    }
    return true;
  }

  public boolean checkLow() {
    int countLow = COUNT_WATER_MARK.low();
    long l = count.get();
    if (l < countLow) {
      isWritable.set(true);
      return true;
    }
    return false;
  }

  public void await(String message) {
    // 按照条件阻塞生产.
    lock.lock();
    try {
      while (!isReadable()) {
        countLimitLockCondition.await();
      }
      count.incrementAndGet();
      byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
      int i = bytes.length;
      size.addAndGet(i);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
  }

  public boolean isReadable() {
    int countHigh = COUNT_WATER_MARK.high();
    int sizeHigh = SIZE_WATER_MARK.high();
    long l = count.get();
    long l1 = size.get();
    if (l >= countHigh || l1 >= sizeHigh) {
      isReadable.set(false);
      return false;
    } else {
      isReadable.set(true);
      return true;
    }
  }

  public void signalAll(long s, long k) {
    lock.lock();
    try {
      long l = count.get();
      if (l != 0) {
        // 减去一批条数.
        count.addAndGet(-s);
      }
      if (l != 0) {
        size.addAndGet(-s * k);
      }
      countLimitLockCondition.signalAll();
    } finally {
      lock.unlock();
    }
  }
}
