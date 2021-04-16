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
 * @version 2021-04-06 14:59
 * @since 2021-04-06 14:59:00
 */
public class DeleteStrategy extends AbstractStrategy {

  public DeleteStrategy() {
    //
  }

  public void delete(final File source) {
    try {
      Files.delete(source.toPath());
    } catch (IOException e) {
      throw new StudyJuliRuntimeException(e);
    }
  }
}
