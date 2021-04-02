package org.study.juli.examples.example5;

import org.study.juli.logging.spi.Log;
import org.study.juli.logging.base.LogFactory;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class Examples3 {

  private static final Log log = LogFactory.getLog(Examples3.class);

  public void main(int i) {
    log.error("Examples3>error>>>>我要去的日志文件是5example,当前的日志计数是:{}", "" + i);
    log.info("Examples3>info>>>>我要去的日志文件是5example,当前的日志计数是:{}", "" + i);
    log.warn("Examples3>warn>>>>我要去的日志文件是5example,当前的日志计数是:{}", "" + i);
    log.fatal("Examples3>fatal>>>>我要去的日志文件是5example,当前的日志计数是:{}", "" + i);
    log.debug("Examples3>debug>>>>我要去的日志文件是5example,当前的日志计数是:{}", "" + i);
    log.trace("Examples3>trace>>>>我要去的日志文件是5example,当前的日志计数是:{}", "" + i);
  }
}
