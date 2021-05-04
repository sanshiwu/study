package org.study.juli.logging.api.logger;

import org.study.juli.logging.api.handler.Handler;
import org.study.juli.logging.api.metainfo.Level;
import org.study.juli.logging.api.metainfo.LogRecord;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface Logger {

  void addHandler(Handler rootHandler);

  void setLevel(Level level);

  String getName();

  Handler[] getHandlers();

  Level getLevel();

  void setUseParentHandlers(boolean b);

  void logp(LogRecord lr);

  void logp(Level warning, String name, String go, String s, Object[] objects);

  void logp(Level warning, String name, String go, String s, Object object);

  void log(final Level level, final String msg, final Throwable thrown);
  //
}
