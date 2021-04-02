package org.study.juli.logging.pressure.water;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 写数据.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public final class FileOutboundBuffer extends AbstractBoundBuffer {
  /** 未来可以考虑使用AtomicLongFieldUpdater. */
  private static final AtomicLong count = new AtomicLong(0L);
  /** 未来可以考虑使用AtomicLongFieldUpdater. */
  private static final AtomicLong size = new AtomicLong(0L);
  /** 控制写水位的锁. */
  private static final ReentrantLock lock = new ReentrantLock();
  /** Condition for waiting takes */
  private static final Condition countLimitLockCondition = lock.newCondition();

  private static final AtomicBoolean isReadable = new AtomicBoolean(true);

  private FileOutboundBuffer() {
    //
  }

  public static boolean isReadable() {
    int countHigh = countWaterMark.high();
    int sizeHigh = sizeWaterMark.high();
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

  public static void await(String message) {
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

  public static void signalAll(long s, long k) {
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
