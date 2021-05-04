package org.study.agent.examples.first.example4;

import org.study.juli.logging.base.LogFactory;
import org.study.juli.logging.spi.Log;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class example6 {

  /**
   * This is a class description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public static class Examples6 {

    private static final Log log = LogFactory.getLog(Examples6.class);

    public void main(int i) {
      log.error("Examples6>error>>>>我要去的日志文件是4study,当前的日志计数是:{}", "" + i);
      log.info("Examples6>info>>>>我要去的日志文件是4study,当前的日志计数是:{}", "" + i);
      log.warn("Examples6>warn>>>>我要去的日志文件是4study,当前的日志计数是:{}", "" + i);
      log.fatal("Examples6>fatal>>>>我要去的日志文件是4study,当前的日志计数是:{}", "" + i);
      log.debug("Examples6>debug>>>>我要去的日志文件是4study,当前的日志计数是:{}", "" + i);
      log.trace("Examples6>trace>>>>我要去的日志文件是4study,当前的日志计数是:{}", "" + i);
    }
  }
}
