package org.study.juli.examples.example;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-03-24 12:57
 * @since 2021-03-24 12:57:00
 */
public class StudyRejectedPolicy implements RejectedExecutionHandler {

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    //
  }
}
