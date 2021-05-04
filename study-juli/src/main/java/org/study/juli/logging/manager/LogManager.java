package org.study.juli.logging.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.study.juli.logging.logger.JuliLogger;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface LogManager {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param logger .
   * @return b.
   * @author admin
   */
  boolean addLogger(JuliLogger logger);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @return JuliLogger.
   * @author admin
   */
  JuliLogger getLogger(String name);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @return Enumeration.
   * @author admin
   */
  Enumeration<String> getLoggerNames();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param name .
   * @return String.
   * @author admin
   */
  String getProperty(String name);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param ins .
   * @throws IOException .
   * @throws SecurityException .
   * @author admin
   */
  void readConfiguration(InputStream ins) throws IOException, SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  void readConfiguration() throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  void reset() throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param mapper .
   * @throws IOException .
   * @author admin
   */
  void updateConfiguration(Function<String, BiFunction<String, String, String>> mapper)
      throws IOException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param ins .
   * @param mapper .
   * @throws IOException .
   * @author admin
   */
  void updateConfiguration(
      InputStream ins, Function<String, BiFunction<String, String, String>> mapper)
      throws IOException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @throws SecurityException .
   * @author admin
   */
  void checkAccess() throws SecurityException;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param listener .
   * @return AbstractLogManager
   * @author admin
   */
  AbstractLogManager addConfigurationListener(Runnable listener);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param listener .
   * @author admin
   */
  void removeConfigurationListener(Runnable listener);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void checkPermission();
}
