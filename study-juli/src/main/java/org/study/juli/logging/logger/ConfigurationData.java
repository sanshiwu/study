package org.study.juli.logging.logger;

import java.util.ArrayList;
import java.util.List;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.handler.Handler;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-02 15:03
 * @since 2021-04-02 15:03:00
 */
@SuppressWarnings({"java:S2250"})
public class ConfigurationData {
  private boolean useParentHandlers;
  private Filter filter;
  private Level levelObject;
  private int levelValue;
  private final List<Handler> handlers = new ArrayList<>(16);

  ConfigurationData() {
    useParentHandlers = true;
    levelObject = Level.INFO;
    levelValue = Level.INFO.intValue();
  }

  void setUseParentHandlers(boolean flag) {
    useParentHandlers = flag;
  }

  void setFilter(Filter f) {
    filter = f;
  }

  void setLevelObject(Level l) {
    levelObject = l;
  }

  void setLevelValue(int v) {
    levelValue = v;
  }

  void addHandler(Handler h) {
    handlers.add(h);
  }

  void removeHandler(Handler h) {
    handlers.remove(h);
  }

  public boolean isUseParentHandlers() {
    return useParentHandlers;
  }

  public Filter getFilter() {
    return filter;
  }

  public Level getLevelObject() {
    return levelObject;
  }

  public int getLevelValue() {
    return levelValue;
  }

  public List<Handler> getHandlers() {
    return new ArrayList<>(handlers);
  }
}
