package org.study.juli.logging.strategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.study.juli.logging.exception.StudyJuliRuntimeException;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class DeleteStrategy extends AbstractStrategy {

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public DeleteStrategy() {
    //
  }

  /**
   * .
   *
   * <p>Another description after blank line.
   *
   * @param source .
   * @author admin
   */
  public void delete(final File source) {
    try {
      Files.delete(source.toPath());
    } catch (IOException e) {
      throw new StudyJuliRuntimeException(e);
    }
  }
}
