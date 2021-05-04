package org.study.juli.logging.context;

import org.study.juli.logging.exception.StudyJuliRuntimeException;
import org.study.juli.logging.queue.StudyHandler;
import org.study.juli.logging.thread.StudyThread;

/**
 * 一个抽象的上下文对象,主要提供一些方法,用于监控线程的运行情况.
 *
 * <p>一般来说,上下文对象主要保存一些系统信息.
 *
 * @author admin
 */
public abstract class AbstractStudyContext implements StudyContext {

  /**
   * 用于在执行方法前后增加开始时间和结束时间.
   *
   * <p>用于监控方法的执行时间.
   *
   * @param event 作为handler的参数传递.
   * @param handler 一个处理器,用于执行具体业务逻辑.
   * @author admin
   */
  @Override
  public final <T> void dispatch(final T event, final StudyHandler<T> handler) {
    try {
      // 增加开始时间.
      beginDispatch();
      // 执行具体业务,传递event参数.
      handler.handle(event);
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    } finally {
      // 增加结束时间.
      endDispatch();
    }
  }

  /**
   * 用于在执行方法前后增加开始时间和结束时间.
   *
   * <p>用于监控方法的执行时间.
   *
   * @param handler 一个Runnable任务,用于执行具体业务逻辑.
   * @author admin
   */
  @Override
  public final void dispatch(final Runnable handler) {
    try {
      // 增加开始时间.
      beginDispatch();
      // 执行具体业务.
      handler.run();
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    } finally {
      // 增加结束时间.
      endDispatch();
    }
  }

  /**
   * 首先判断,当前线程是不是StudyThread.
   *
   * <p>不处理main线程.
   *
   * @author admin
   */
  @Override
  public void beginDispatch() {
    // 获取当前线程.
    Thread thread = Thread.currentThread();
    // 当前线程是不是StudyThread.
    if (thread instanceof StudyThread) {
      // 得到当前真实线程.
      StudyThread th = (StudyThread) thread;
      // 设置开始时间和当前上下文对象.
      th.beginEmission(this);
    }
  }

  /**
   * 首先判断,当前线程是不是StudyThread.
   *
   * <p>不处理main线程.
   *
   * @author admin
   */
  @Override
  public void endDispatch() {
    // 获取当前线程.
    Thread thread = Thread.currentThread();
    // 当前线程是不是StudyThread.
    if (thread instanceof StudyThread) {
      // 得到当前真实线程.
      StudyThread th = (StudyThread) thread;
      // 设置结束时间和将前上下文对象设置成空.
      th.endEmission();
    }
  }

  /**
   * 用于在执行方法前后增加开始时间和结束时间.
   *
   * <p>用于监控方法的执行时间.
   *
   * @param unique .
   * @param event 作为handler的参数传递.
   * @param handler 一个处理器,用于执行具体业务逻辑.
   * @author admin
   */
  @Override
  public final <T> void dispatchV2(
      final String unique, final T event, final StudyHandler<T> handler) {
    try {
      // 增加开始时间.
      beginDispatchV2(unique);
      // 执行具体业务,传递event参数.
      handler.handle(event);
    } catch (Exception e) {
      throw new StudyJuliRuntimeException(e);
    } finally {
      // 增加结束时间.
      endDispatchV2();
    }
  }

  /**
   * 首先判断,当前线程是不是StudyThread.
   *
   * <p>不处理main线程.
   *
   * @author admin
   */
  @Override
  public void beginDispatchV2(final String unique) {
    // 获取当前线程.
    Thread thread = Thread.currentThread();
    // 当前线程是不是StudyThread.
    if (thread instanceof StudyThread) {
      // 得到当前真实线程.
      StudyThread th = (StudyThread) thread;
      // 设置开始时间和当前上下文对象.
      th.beginEmissionV2(unique, this);
    }
  }

  /**
   * 首先判断,当前线程是不是StudyThread.
   *
   * <p>不处理main线程.
   *
   * @author admin
   */
  @Override
  public void endDispatchV2() {
    // 获取当前线程.
    Thread thread = Thread.currentThread();
    // 当前线程是不是StudyThread.
    if (thread instanceof StudyThread) {
      // 得到当前真实线程.
      StudyThread th = (StudyThread) thread;
      // 设置结束时间和将前上下文对象设置成空.
      th.endEmissionV2();
    }
  }
}
