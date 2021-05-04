package org.study.juli.logging.core.strategy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.study.juli.logging.api.exception.StudyJuliRuntimeException;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class GzipCompressStrategy extends AbstractCompressStrategy {
  /** . */
  private static final int BUF_SIZE = 8192;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public GzipCompressStrategy() {
    //
  }

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
  @Override
  public void execute(final File source, final File destination, final int compressionLevel) {
    try (FileInputStream fis = new FileInputStream(source);
        OutputStream fos = new FileOutputStream(destination);
        OutputStream gzipOut = new GzipOutputStreamByLevel(fos, BUF_SIZE, compressionLevel);
        OutputStream os = new BufferedOutputStream(gzipOut, BUF_SIZE)) {
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
