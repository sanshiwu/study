package org.study.juli.logging.utils;

import java.lang.reflect.Constructor;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-02 13:45
 * @since 2021-04-02 13:45:00
 */
@SuppressWarnings({"java:S1452", "java:S2658"})
public final class ClassLoadingUtils {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private ClassLoadingUtils() {
    //
  }

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public static Constructor<?> constructor(String className) throws Exception {
    ClassLoader systemClassLoader = Thread.currentThread().getContextClassLoader();
    Class<?> classObj = systemClassLoader.loadClass(className);
    return classObj.getConstructor();
  }
}
