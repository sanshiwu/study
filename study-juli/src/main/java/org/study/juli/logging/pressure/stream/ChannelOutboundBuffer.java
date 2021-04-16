package org.study.juli.logging.pressure.stream;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import org.study.juli.logging.pressure.water.WriteBufferWaterMark;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-07 19:38
 * @since 2021-04-07 19:38:00
 */
public class ChannelOutboundBuffer {

  static final int CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD = 96;

  private int flushed;

  private int nioBufferCount;

  private long nioBufferSize;

  private boolean inFail;

  private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER =
      AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");

  @SuppressWarnings("UnusedDeclaration")
  private volatile long totalPendingSize;

  private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> UNWRITABLE_UPDATER =
      AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "unwritable");

  @SuppressWarnings("UnusedDeclaration")
  private volatile int unwritable;

  private volatile Runnable fireChannelWritabilityChangedTask;

  public void addMessage(Object msg, int size) {
    incrementPendingOutboundBytes(1, false);
  }

  public void addFlush() {
    decrementPendingOutboundBytes(2, false, true);
  }

  void incrementPendingOutboundBytes(long size) {
    incrementPendingOutboundBytes(size, true);
  }

  private void incrementPendingOutboundBytes(long size, boolean invokeLater) {
    if (size == 0) {
      return;
    }

    long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
    if (newWriteBufferSize > new WriteBufferWaterMark(1, 2, 3).high()) {
      setUnwritable(invokeLater);
    }
  }

  void decrementPendingOutboundBytes(long size) {
    decrementPendingOutboundBytes(size, true, true);
  }

  private void decrementPendingOutboundBytes(
      long size, boolean invokeLater, boolean notifyWritability) {
    if (size == 0) {
      return;
    }

    long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
    if (notifyWritability && newWriteBufferSize < new WriteBufferWaterMark(1, 2, 3).low()) {
      setWritable(invokeLater);
    }
  }

  private static long total(Object msg) {
    return -1;
  }

  public int nioBufferCount() {
    return nioBufferCount;
  }

  public long nioBufferSize() {
    return nioBufferSize;
  }

  public boolean isWritable() {
    return unwritable == 0;
  }

  public boolean getUserDefinedWritability(int index) {
    return (unwritable & writabilityMask(index)) == 0;
  }

  public void setUserDefinedWritability(int index, boolean writable) {
    if (writable) {
      setUserDefinedWritability(index);
    } else {
      clearUserDefinedWritability(index);
    }
  }

  private void setUserDefinedWritability(int index) {
    final int mask = ~writabilityMask(index);
    for (; ; ) {
      final int oldValue = unwritable;
      final int newValue = oldValue & mask;
      if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
        if (oldValue != 0 && newValue == 0) {}

        break;
      }
    }
  }

  private void clearUserDefinedWritability(int index) {
    final int mask = writabilityMask(index);
    for (; ; ) {
      final int oldValue = unwritable;
      final int newValue = oldValue | mask;
      if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
        if (oldValue == 0 && newValue != 0) {}

        break;
      }
    }
  }

  private static int writabilityMask(int index) {
    if (index < 1 || index > 31) {
      throw new IllegalArgumentException("index: " + index + " (expected: 1~31)");
    }
    return 1 << index;
  }

  private void setWritable(boolean invokeLater) {
    for (; ; ) {
      final int oldValue = unwritable;
      final int newValue = oldValue & ~1;
      if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
        if (oldValue != 0 && newValue == 0) {}

        break;
      }
    }
  }

  private void setUnwritable(boolean invokeLater) {
    for (; ; ) {
      final int oldValue = unwritable;
      final int newValue = oldValue | 1;
      if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
        if (oldValue == 0 && newValue != 0) {}

        break;
      }
    }
  }

  public int size() {
    return flushed;
  }

  public boolean isEmpty() {
    return flushed == 0;
  }

  public long totalPendingWriteBytes() {
    return totalPendingSize;
  }

  public long bytesBeforeUnwritable() {
    long bytes = new WriteBufferWaterMark(1, 2, 3).high() - totalPendingSize;
    if (bytes > 0) {
      return isWritable() ? bytes : 0;
    }
    return 0;
  }

  public long bytesBeforeWritable() {
    long bytes = totalPendingSize - new WriteBufferWaterMark(1, 2, 3).low();
    if (bytes > 0) {
      return isWritable() ? 0 : bytes;
    }
    return 0;
  }
}
