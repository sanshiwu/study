package org.study.juli.examples;

import org.study.juli.logging.base.Log;
import org.study.juli.logging.base.LogFactory;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class Example5 {

  /**
   * This is a class description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public static class Examples5 {

    private static final Log log = LogFactory.getLog(Examples5.class);

    public void main(int i) {
      log.error("Examples5>error>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.info("Examples5>info>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.warn("Examples5>warn>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.fatal("Examples5>fatal>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.debug("Examples5>debug>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.trace("Examples5>trace>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    }
  }
}
