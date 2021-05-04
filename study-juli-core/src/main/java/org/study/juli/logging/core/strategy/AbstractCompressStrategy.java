package org.study.juli.logging.core.strategy;

import java.io.File;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public abstract class AbstractCompressStrategy extends AbstractStrategy {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param source .
   * @param destination .
   * @param compressionLevel .
   * @author admin
   */
  abstract void execute(File source, File destination, int compressionLevel);
}
