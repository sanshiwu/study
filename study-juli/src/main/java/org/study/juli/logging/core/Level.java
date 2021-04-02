package org.study.juli.logging.core;

import java.util.HashMap;
import java.util.Map;

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

  private static final Map<String, Level> standardLevels = new HashMap<>(16);

  static {
    standardLevels.put("OFF", OFF);
    standardLevels.put("SEVERE", SEVERE);
    standardLevels.put("WARNING", WARNING);
    standardLevels.put("INFO", INFO);
    standardLevels.put("CONFIG", CONFIG);
    standardLevels.put("FINE", FINE);
    standardLevels.put("FINER", FINER);
    standardLevels.put("FINEST", FINEST);
    standardLevels.put("ALL", ALL);
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

  public String getLocalizedName() {
    return this.getName();
  }

  public static Level findLevel(String name) {
    return standardLevels.get(name);
  }

  @Override
  public final String toString() {
    return name;
  }

  public final int intValue() {
    return value;
  }
}
