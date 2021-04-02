package org.study.juli.logging.filter;

import java.util.function.BooleanSupplier;
import org.study.juli.logging.core.LogRecord;

public interface Filter extends BooleanSupplier {
  boolean isLoggable(LogRecord record);

  @Override
  default boolean getAsBoolean() {
    return false;
  }
}
