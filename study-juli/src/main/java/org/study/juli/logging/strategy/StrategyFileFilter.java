package org.study.juli.logging.strategy;

import java.io.File;
import java.io.FileFilter;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-06 22:13
 * @since 2021-04-06 22:13:00
 */
public class StrategyFileFilter implements FileFilter {

  @Override
  public boolean accept(File paramFile) {
    String name = paramFile.getName();
    return name.endsWith(".log");
  }
}
