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
 * @version 2021-04-01 13:48
 * @since 2021-04-01 13:48:00
 */
public interface LogManager {

  boolean addLogger(JuliLogger logger);

  JuliLogger getLogger(String name);

  Enumeration<String> getLoggerNames();

  String getProperty(String name);

  void readConfiguration() throws SecurityException;

  void reset() throws SecurityException;

  void readConfiguration(InputStream ins) throws IOException, SecurityException;

  void updateConfiguration(Function<String, BiFunction<String, String, String>> mapper)
      throws IOException;

  void updateConfiguration(
      InputStream ins, Function<String, BiFunction<String, String, String>> mapper)
      throws IOException;

  void checkAccess() throws SecurityException;

  AbstractLogManager addConfigurationListener(Runnable listener);

  void removeConfigurationListener(Runnable listener);

  void checkPermission();
}
