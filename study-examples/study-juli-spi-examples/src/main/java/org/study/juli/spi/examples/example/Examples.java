package org.study.juli.spi.examples.example;

import org.study.juli.logging.spi.Log;
import org.study.juli.logging.base.LogFactory;
import org.study.juli.spi.examples.Example5.Examples5;
import org.study.juli.spi.examples.example1.Test1;
import org.study.juli.spi.examples.example2.Examples1;
import org.study.juli.spi.examples.example2.Test2;
import org.study.juli.spi.examples.example3.Examples3;
import org.study.juli.spi.examples.example3.Test3;
import org.study.juli.spi.examples.example4.Examples4;
import org.study.juli.spi.examples.example4.Test4;
import org.study.juli.spi.examples.example4.example6.Examples6;
import org.study.juli.spi.examples.example5.Test5;
import org.study.juli.spi.examples.example6.Example6;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class Examples {

  private static final Log log = LogFactory.getLog(Examples.class);

  public static void main(String[] args) {
    long s = System.currentTimeMillis();
    Examples1 examples2 = new Examples1();
    Examples3 examples3 = new Examples3();
    Examples4 examples4 = new Examples4();
    Test test = new Test();
    Examples5 examples5 = new Examples5();
    Examples6 examples6 = new Examples6();
    Example6 example6 = new Example6();
    Test1 test1 = new Test1();
    Test2 test2 = new Test2();
    Test3 test3 = new Test3();
    Test4 test4 = new Test4();
    Test5 test5 = new Test5();
    // 共34万8千条日志.
    for (int i = 0; i < 1000; i++) {
      // 输入到日志1example文件.
      test1.test(i);
      // 输入到日志2example文件.
      test2.test(i);
      // 输入到日志3example文件.
      test3.test(i);
      // 输入到日志4example文件.
      test4.test(i);
      // 输入到日志5example文件.
      test5.test(i);
      // 输入到日志2study文件.
      examples2.main(i);
      // 输入到日志3study文件.
      examples3.main(i);
      // 输入到日志4study文件.
      examples4.main(i);
      // 输入到日志2study文件.
      test.main(i);
      // 输入到日志2study文件.
      examples5.main(i);
      // 输入到日志4study文件.
      examples6.main(i);
      // 输入到日志study_juli文件.
      example6.main(i);
      // 输入到日志1study文件.
      log.error("Examples>error>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.info("Examples>info>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.warn("Examples>warn>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.fatal("Examples>fatal>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.debug("Examples>debug>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
      log.trace("Examples>trace>>>>我要去的日志文件是1study,当前的日志计数是:{}", "" + i);
    }
    long e1 = System.currentTimeMillis();
    System.out.println("消耗的时间:" + (e1 - s) / 1000 + "秒");
    try {
      Thread.sleep(15000000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
