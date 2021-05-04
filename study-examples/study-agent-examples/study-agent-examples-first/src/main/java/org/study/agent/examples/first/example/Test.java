package org.study.agent.examples.first.example;

import org.study.juli.logging.base.LogFactory;
import org.study.juli.logging.spi.Log;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class Test {

  private static final Log log = LogFactory.getLog(Test.class);

  public void main(int i) {
    log.error("Test>error>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    log.info("Test>info>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    log.warn("Test>warn>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    log.fatal("Test>fatal>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    log.debug("Test>debug>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    log.trace("Test>trace>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
  }
}
