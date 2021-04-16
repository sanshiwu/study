package org.study.juli.logging.strategy;

import java.io.File;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-06 14:04
 * @since 2021-04-06 14:04:00
 */
public abstract class AbstractCompressStrategy extends AbstractStrategy {

  abstract void execute(final File source, final File destination, final int compressionLevel);
}
