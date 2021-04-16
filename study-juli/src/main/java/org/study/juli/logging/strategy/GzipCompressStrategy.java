package org.study.juli.logging.strategy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.study.juli.logging.exception.StudyJuliRuntimeException;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-06 14:21
 * @since 2021-04-06 14:21:00
 */
public class GzipCompressStrategy extends AbstractCompressStrategy {
  private static final int BUF_SIZE = 8192;

  public GzipCompressStrategy() {
    //
  }

  @Override
  public void execute(final File source, final File destination, final int compressionLevel) {
    try (final FileInputStream fis = new FileInputStream(source);
        final OutputStream fos = new FileOutputStream(destination);
        final OutputStream gzipOut = new GzipOutputStreamByLevel(fos, BUF_SIZE, compressionLevel);
        final OutputStream os = new BufferedOutputStream(gzipOut, BUF_SIZE)) {
      final byte[] buff = new byte[BUF_SIZE];
      int n;
      while ((n = fis.read(buff)) != -1) {
        os.write(buff, 0, n);
      }
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    }
  }
}
