package org.study.agent.examples.first;

import org.study.juli.logging.spi.Log;
import org.study.juli.logging.base.LogFactory;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class TestMain1122 extends Abc {

  private static final Log log = LogFactory.getLog(TestMain1122.class);

  @Override
  public void initializeStatic3() {

    System.out.println("abs");
    log.error("abc");
  }

  @Override
  public void initializeStatic4(final String abc) {

    System.out.println("abs:" + abc);
    log.error("abc");
  }
}
