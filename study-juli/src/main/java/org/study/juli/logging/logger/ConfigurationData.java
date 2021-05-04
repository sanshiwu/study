package org.study.juli.logging.logger;

import java.util.ArrayList;
import java.util.List;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.handler.Handler;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.@SuppressWarnings({"java:S2250"})
 *
 * @author admin
 */
public class ConfigurationData {
  /** . */
  private final List<Handler> handlers = new ArrayList<>(16);
  /** . */
  private boolean useParentHandlers;
  /** . */
  private Filter filter;
  /** . */
  private Level levelObject;
  /** . */
  private int levelValue;

  ConfigurationData() {
    useParentHandlers = true;
    levelObject = Level.INFO;
    levelValue = Level.INFO.intValue();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param h .
   * @author admin
   */
  void addHandler(final Handler h) {
    handlers.add(h);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param h .
   * @author admin
   */
  void removeHandler(final Handler h) {
    handlers.remove(h);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return boolean .
   * @author admin
   */
  public boolean isUseParentHandlers() {
    return useParentHandlers;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param flag .
   * @author admin
   */
  void setUseParentHandlers(final boolean flag) {
    useParentHandlers = flag;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Filter .
   * @author admin
   */
  public Filter getFilter() {
    return filter;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param f .
   * @author admin
   */
  void setFilter(final Filter f) {
    filter = f;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Level .
   * @author admin
   */
  public Level getLevelObject() {
    return levelObject;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param l .
   * @author admin
   */
  void setLevelObject(final Level l) {
    levelObject = l;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return int .
   * @author admin
   */
  public int getLevelValue() {
    return levelValue;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param v .
   * @author admin
   */
  void setLevelValue(final int v) {
    levelValue = v;
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return List Handler.
   * @author admin
   */
  public List<Handler> getHandlers() {
    return new ArrayList<>(handlers);
  }
}
