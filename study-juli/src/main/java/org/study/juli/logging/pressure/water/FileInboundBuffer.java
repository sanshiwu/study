package org.study.juli.logging.pressure.water;

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
  /** Condition for waiting takes */
  private final WriteBufferWaterMark countWaterMark = new WriteBufferWaterMark(84000, 85000, 0);
  /** Condition for waiting takes */
  private final WriteBufferWaterMark sizeWaterMark =
      new WriteBufferWaterMark(1 * 1024 * 1024, 2 * 1024 * 1024, 1);
  /** 未来可以考虑使用AtomicLongFieldUpdater. */
  private final AtomicLong count = new AtomicLong(0L);
  /** 未来可以考虑使用AtomicLongFieldUpdater. */
  private final AtomicLong size = new AtomicLong(0L);
  /** 控制写水位的锁. */
  private final ReentrantLock lock = new ReentrantLock();
  /** Condition for waiting takes */
  private final Condition countLimitLockCondition = lock.newCondition();

  private final AtomicBoolean isWritable = new AtomicBoolean(true);

  public FileInboundBuffer() {
    //
  }

  public boolean isWritable() {
    return isWritable.get();
  }

  public AtomicLong getCount() {
    return count;
  }

  public boolean checkHigh() {
    int countHigh = countWaterMark.high();
    // int sizeHigh = sizeWaterMark.high();
    long l = count.get();
    // long l1 = size.get();
    if (l >= countHigh) { // || l1 >= sizeHigh
      isWritable.set(false);
      return false;
    }
    return true;
  }

  public boolean checkLow() {
    int countLow = countWaterMark.low();
    // int sizeLow = sizeWaterMark.low();
    long l = count.get();
    // long l1 = size.get();
    if (l < countLow) { // && l1 < sizeLow
      isWritable.set(true);
      return true;
    }
    return false;
  }

  public synchronized void add(String message) {
    count.incrementAndGet();
    int countHigh = countWaterMark.high();
    long l = count.get();
    // long l1 = size.get();
    if (l >= countHigh) {
      //isWritable.set(false);
      System.out.println(
          "当前无法写入,达到限制条数,"
              + count.get()
              + ",限制字节"
              + size.get()
              + ",水位线限制条数"
              + countWaterMark.high()
              + ",水位线限制字节 "
              + sizeWaterMark.high());
    }
    /*    if (!checkHigh()) {
    System.out.println(
        "当前无法写入,达到限制条数,"
            + count.get()
            + ",限制字节"
            + size.get()
            + ",水位线限制条数"
            + countWaterMark.high()
            + ",水位线限制字节 "
            + sizeWaterMark.high());*/
  }

  public void await(String message) {
    // 按照条件阻塞生产.
    lock.lock();
    try {
      System.out.println(Thread.currentThread().getName() + "当前线程被锁定.");
      countLimitLockCondition.await();
      // byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
      // int i = bytes.length;
      // size.addAndGet(i);
      /* System.out.println(
      "当前可以写入,没有达到限制条数,"
          + count.get()
          + ",限制字节"
          + size.get()
          + ",水位线限制条数"
          + countWaterMark.high()
          + ",水位线限制字节 "
          + sizeWaterMark.high());*/
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
  }

  public void signalAll(int s, int k) {
    lock.lock();
    try {
      long l = count.get();
      if (l > 0) {
        // 减去一批条数.
        count.addAndGet(-s);
      } else {
        count.set(0L);
      }
      /*     long l1 = size.get();
      if (l > 0) {
        size.addAndGet(-k);
      }*/
      /* System.out.println(
      "当前可以消费,没有达到限制条数,"
          + count.get()
          + ",限制字节"
          + size.get()
          + ",水位线限制条数"
          + countWaterMark.high()
          + ",水位线限制字节 "
          + sizeWaterMark.high());*/
      int countLow = countWaterMark.low();
      if (l < countLow) {
        //isWritable.set(true);
        countLimitLockCondition.signalAll();
        System.out.println("开始唤醒");
      }
    } finally {
      lock.unlock();
    }
  }
}
