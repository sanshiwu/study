package org.study.juli.logging.strategy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-06 14:58
 * @since 2021-04-06 14:58:00
 */
public class GzipOutputStreamByLevel extends GZIPOutputStream {

  public GzipOutputStreamByLevel(OutputStream out, int bufSize, int level) throws IOException {
    super(out, bufSize);
    def.setLevel(level);
  }
}
