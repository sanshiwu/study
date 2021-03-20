package org.study.juli.spi.examples.plugin;

import java.util.StringJoiner;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ExtendedThrowablePatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;
import org.apache.logging.log4j.util.Strings;

/**
 * json throwable模式转换器 java异常堆栈使用json格式进行打印,默认是多行的, 不符合json的语法,会报错 问题是,这个类偶尔可以正常解析，但是大部分时候不可以，找不到这个类.
 *
 * @author admin
 */
@Plugin(name = "JsonThrowablePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys("aj")
public final class JsonThrowablePatternConverter extends ThrowablePatternConverter {

  /**
   * .
   */
  private final ExtendedThrowablePatternConverter throwablePatternConverter;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param config  .
   * @param options .
   * @author admin
   */
  private JsonThrowablePatternConverter(final Configuration config, final String[] options) {
    super("JsonThrowablePatternConverter", "throwable", options, config);
    throwablePatternConverter = ExtendedThrowablePatternConverter.newInstance(config, options);
  }

  /**
   * This is a method description.
   *
   * @param config  This is a param description.
   * @param options This is a param description.
   * @return This is a return description.
   * @author admin
   */
  public static JsonThrowablePatternConverter newInstance(
      final Configuration config, final String[] options) {
    return new JsonThrowablePatternConverter(config, options);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private static String asJson(final String line) {
    return "\"" + line + "\"";
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void format(final LogEvent event, final StringBuilder toAppendTo) {
    final String consoleStacktrace = this.formatStacktrace(event);
    if (Strings.isNotEmpty(consoleStacktrace)) {
      final String jsonStacktrace = this.formatJson(consoleStacktrace);
      toAppendTo.append(", ");
      toAppendTo.append(jsonStacktrace);
    }
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private String formatStacktrace(final LogEvent event) {
    final StringBuilder stringBuilder = new StringBuilder();
    this.throwablePatternConverter.format(event, stringBuilder);
    return stringBuilder.toString();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private String formatJson(final String consoleStacktrace) {
    final String lineSeparator = this.options.getSeparator() + "\t|" + this.options.getSeparator();
    final String[] split = consoleStacktrace.split(lineSeparator);
    final StringJoiner stringJoiner = new StringJoiner(",\n", "\n\"stacktrace\": [", "]");
    for (final String line : split) {
      stringJoiner.add(asJson(line));
    }
    return stringJoiner.toString();
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public boolean handlesThrowable() {
    return true;
  }
}
