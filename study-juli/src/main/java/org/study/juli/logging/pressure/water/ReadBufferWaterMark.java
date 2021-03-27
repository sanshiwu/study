package org.study.juli.logging.pressure.water;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class ReadBufferWaterMark {

  private final int low;
  private final int high;

  private final int type;

  ReadBufferWaterMark(int low, int high, int type) {
    this.low = low;
    this.high = high;
    this.type = type;
  }

  public int low() {
    return low;
  }

  public int high() {
    return high;
  }

  @Override
  public String toString() {
    StringBuilder builder =
        new StringBuilder(55)
            .append("ReadBufferWaterMark(low: ")
            .append(low)
            .append(", high: ")
            .append(high)
            .append(")");
    return builder.toString();
  }
}