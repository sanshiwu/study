package org.study.agent.examples.first;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.study.juli.logging.base.LogFactory;
import org.study.juli.logging.spi.Log;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public class TestMain {

  private static final Log log = LogFactory.getLog(TestMain.class);

  /**
   * This is a method description.
   *
   * @param args 入口方法参数传递.
   * @author Admin
   */
  public static void main(final String[] args) {
    try {
      log.info("xxxxxxxxxxxxxxx");
      // works with ZonedDateTime
      final ZonedDateTime zdt = ZonedDateTime.now();

      System.out.println(zdt.format(DateTimeFormatter.ISO_INSTANT));

      System.out.println(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

      System.out.println(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));

      System.out.println(zdt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
      System.out.println(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      System.out.println(zdt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      System.out.println(zdt.format(DateTimeFormatter.ISO_DATE_TIME));
      final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      final String format = pattern.format(zdt);
      System.out.println(format);

      final DateTimeFormatter pattern1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
      final String format1 = pattern1.format(zdt);
      System.out.println(format1);

      // LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
      // final LocalDateTime now1 = LocalDateTime.now(Clock.systemDefaultZone());
      // final DateTimeFormatter pattern1 =
      // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSZZ");
      // final String format1 = pattern1.format(now1);
      // System.out.println(format1);

      TestMain.initializeStatic566(222, "ddd", "xxx", "aaa");
      final Abc abc = new TestMain1122();
      TestMain.initializeStatic2();
      TestMain.initializeStatic("2222222");
      abc.initializeStatic3();
      abc.initializeStatic4("4444");
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      final InetAddress address = TestMain.noFail(() -> InetAddress.getByName("www.google.com"));
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      TestMain.noFail(() -> new Socket("www.google.con", 80));
    } catch (final Exception e) {
      e.printStackTrace();
    }

    try {
      TestMain.noFail(() -> new ServerSocket(8080));
    } catch (final Exception e) {
      e.printStackTrace();
    }

    try {
      TestMain.noFail(
          () -> {
            final DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.connect(new InetSocketAddress("www.google.com", 80));
            return udpSocket;
          });
    } catch (final Exception e) {

    }

    try {
      Thread.sleep(9999999);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static <T> T noFail(final Callable<T> action) throws Exception {
    return action.call();
  }

  private static String initializeStatic2() {
    final Properties props = new Properties();
    props.put("security.provider.1", "sun.security.provider.Sun");
    props.put("security.provider.2", "sun.security.rsa.SunRsaSign");
    props.put("security.provider.3", "com.sun.net.ssl.internal.ssl.Provider");
    props.put("security.provider.4", "com.sun.crypto.provider.SunJCE");
    props.put("security.provider.5", "sun.security.jgss.SunProvider");
    props.put("security.provider.6", "com.sun.security.sasl.Provider");

    return "xxxxxxxxxxxxxxxxxxx";
  }

  private static Properties initializeStatic(final String abc) {
    final Properties props = new Properties();
    props.put("security.provider.1", "sun.security.provider.Sun");
    props.put("security.provider.2", "sun.security.rsa.SunRsaSign");
    props.put("security.provider.3", "com.sun.net.ssl.internal.ssl.Provider");
    props.put("security.provider.4", "com.sun.crypto.provider.SunJCE");
    props.put("security.provider.5", "sun.security.jgss.SunProvider");
    props.put("security.provider.6", "com.sun.security.sasl.Provider");
    return props;
  }

  private static Properties initializeStatic566(
      final int id, final String abc, final String abc222, final String abc2223) {
    final Properties props = new Properties();
    props.put("security.provider.1", "sun.security.provider.Sun");
    props.put("security.provider.2", "sun.security.rsa.SunRsaSign");
    props.put("security.provider.3", "com.sun.net.ssl.internal.ssl.Provider");
    props.put("security.provider.4", "com.sun.crypto.provider.SunJCE");
    props.put("security.provider.5", "sun.security.jgss.SunProvider");
    props.put("security.provider.6", "com.sun.security.sasl.Provider");
    return props;
  }
}
