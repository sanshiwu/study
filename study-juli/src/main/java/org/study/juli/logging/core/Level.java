package org.study.juli.logging.core;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class Level {

  public static final Level OFF = new Level("OFF", Integer.MAX_VALUE);

  public static final Level SEVERE = new Level("SEVERE", 1000);

  public static final Level WARNING = new Level("WARNING", 900);

  public static final Level INFO = new Level("INFO", 800);

  public static final Level CONFIG = new Level("CONFIG", 700);

  public static final Level FINE = new Level("FINE", 500);

  public static final Level FINER = new Level("FINER", 400);

  public static final Level FINEST = new Level("FINEST", 300);

  public static final Level ALL = new Level("ALL", Integer.MIN_VALUE);

  private static final Map<String, Level> STANDARD_LEVELS = new HashMap<>(16);

  static {
    STANDARD_LEVELS.put("OFF", OFF);
    STANDARD_LEVELS.put("SEVERE", SEVERE);
    STANDARD_LEVELS.put("WARNING", WARNING);
    STANDARD_LEVELS.put("INFO", INFO);
    STANDARD_LEVELS.put("CONFIG", CONFIG);
    STANDARD_LEVELS.put("FINE", FINE);
    STANDARD_LEVELS.put("FINER", FINER);
    STANDARD_LEVELS.put("FINEST", FINEST);
    STANDARD_LEVELS.put("ALL", ALL);
  }

  private final String name;

  private final int value;

  public Level(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public static Level findLevel(String name) {
    return STANDARD_LEVELS.get(name);
  }

  @Override
  public final String toString() {
    return name;
  }

  public final int intValue() {
    return value;
  }
}
