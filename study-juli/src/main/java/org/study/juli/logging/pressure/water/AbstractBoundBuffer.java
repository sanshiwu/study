package org.study.juli.logging.pressure.water;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractBoundBuffer implements BoundBuffer {
  //
  /** Condition for waiting takes */
  protected static final WriteBufferWaterMark countWaterMark =
      new WriteBufferWaterMark(1000, 1500, 0);
  /** Condition for waiting takes */
  protected static final WriteBufferWaterMark sizeWaterMark =
      new WriteBufferWaterMark(1 * 1024 * 1024, 2 * 1024 * 1024, 1);

  protected AbstractBoundBuffer() {
    //
  }
}
