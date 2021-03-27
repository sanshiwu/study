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
public class FileOutboundBuffer extends AbstractBoundBuffer {
  /** Condition for waiting takes */
  private static final ReadBufferWaterMark countWaterMark =
      new ReadBufferWaterMark(10000, 20000, 0);
  /** Condition for waiting takes */
  private static final ReadBufferWaterMark sizeWaterMark =
      new ReadBufferWaterMark(1 * 1024 * 1024, 2 * 1024 * 1024, 1);
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
        System.out.println(
            "当前无法写入,达到限制条数,"
                + count.get()
                + ",限制字节"
                + size.get()
                + ",水位线限制条数"
                + countWaterMark.high()
                + ",水位线限制字节 "
                + sizeWaterMark.high());
        countLimitLockCondition.await();
      }
      count.incrementAndGet();
      byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
      int i = bytes.length;
      size.addAndGet(i);
      System.out.println(
          "当前可以写入,没有达到限制条数,"
              + count.get()
              + ",限制字节"
              + size.get()
              + ",水位线限制条数"
              + countWaterMark.high()
              + ",水位线限制字节 "
              + sizeWaterMark.high());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
  }

  public static void signalAll(int s, int k) {
    lock.lock();
    try {
      long l = count.get();
      if(l!=0){
        // 减去一批条数.
        count.addAndGet(-s);
      }
      long l1 = size.get();
      if(l!=0){
        size.addAndGet(-s * k);
      }
      System.out.println(
          "当前可以消费,没有达到限制条数,"
              + count.get()
              + ",限制字节"
              + size.get()
              + ",水位线限制条数"
              + countWaterMark.high()
              + ",水位线限制字节 "
              + sizeWaterMark.high());
      countLimitLockCondition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    long s = System.currentTimeMillis();
    String mes = "潇洒啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶帆帆帆帆帆帆帆帆帆帆帆帆帆帆嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎";
    byte[] bytes = mes.getBytes(StandardCharsets.UTF_8);
    int k = bytes.length;
    Runnable r =
        new Runnable() {
          @Override
          public void run() {
            while (true) {
              try {
                Thread.sleep(200);
              } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
              }
              FileOutboundBuffer.signalAll(1500, k);
            }
          }
        };
    new Thread(r).start();

    for (int i = 0; i < 150000; i++) {
      FileOutboundBuffer.await(mes);
    }
    long e = System.currentTimeMillis();
    System.out.println(e - s);
    try {
      Thread.sleep(888888);
    } catch (InterruptedException interruptedException) {
      interruptedException.printStackTrace();
    }
  }
}
