package org.study.juli.logging.api.context;


import org.study.juli.logging.api.worker.StudyWorker;

/**
 * This is a method description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 */
public interface StudyContext {

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handler .
   * @param event .
   * @param <T> .
   * @author admin
   */
  <T> void dispatch(T event, StudyWorker<T> handler);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param handler .
   * @author admin
   */
  void dispatch(Runnable handler);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void beginDispatch();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void endDispatch();

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param unique .
   * @param handler .
   * @param event .
   * @param <T> .
   * @author admin
   */
  <T> void dispatchV2(String unique, T event, StudyWorker<T> handler);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @param unique .
   * @author admin
   */
  void beginDispatchV2(String unique);

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  void endDispatchV2();
}
