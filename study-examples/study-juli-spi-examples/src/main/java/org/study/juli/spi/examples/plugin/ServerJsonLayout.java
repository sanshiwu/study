package org.study.juli.spi.examples.plugin;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * json布局 用来向文件输出json格式的日志消息,代替默认的Json,输出异常堆栈信息到json中.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
@Plugin(
    name = "ServerJsonLayout",
    category = Node.CATEGORY,
    elementType = Layout.ELEMENT_TYPE,
    printObject = true)
public class ServerJsonLayout extends AbstractStringLayout {

  /** . */
  private final PatternLayout patternLayout;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param type .
   * @param charset .
   * @author admin
   */
  protected ServerJsonLayout(final String type, final Charset charset) {
    super(charset);
    patternLayout =
        PatternLayout.newBuilder()
            .withPattern(this.pattern(type))
            .withAlwaysWriteExceptions(true)
            .build();
  }

  /**
   * This is a method description.
   *
   * @param typeName This is a param description.
   * @param charset This is a param description.
   * @return This is a return description.
   * @author admin
   */
  @PluginFactory
  public static ServerJsonLayout createLayout(
      @PluginAttribute("typeName") final String typeName,
      @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset) {
    return new ServerJsonLayout(typeName, charset);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param type .
   * @return Sting .
   * @author admin
   */
  private String pattern(final String type) {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put("type", type);
    map.put("timestamp", "%d{yyyy-MM-dd'T'HH:mm:ss.SSSZZ}");
    map.put("level", "%p");
    map.put("fullClassPath", "%c{1.}");
    map.put("message", "%m");
    return this.createPattern(map);
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param map .
   * @return String .
   * @author admin
   */
  private String createPattern(final Map<String, Object> map) {
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    // 需要优化一下
    String separator = "";
    for (final Map.Entry<String, Object> entry : map.entrySet()) {
      sb.append(separator);
      sb.append(this.inQuotes(entry.getKey()) + ": ");
      sb.append(this.inQuotes(entry.getValue().toString()));
      separator = ",";
    }
    sb.append("%aj");
    sb.append("}");
    // 增加一个换行符号(按照平台获取)
    sb.append(System.lineSeparator());
    return sb.toString();
  }

  /**
   * This is a method description.
   *
   * @param s This is a param description.
   * @return This is a return description.
   * @author admin
   */
  public String inQuotes(final String s) {
    return "\"" + s + "\"";
  }

  /**
   * This is a method description.
   *
   * @param event This is a param description.
   * @return This is a return description.
   * @author admin
   */
  @Override
  public String toSerializable(final LogEvent event) {
    return this.patternLayout.toSerializable(event);
  }
}
