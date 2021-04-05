package org.study.juli.examples.kafka;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.study.juli.logging.core.Level;
import org.study.juli.logging.core.LogRecord;
import org.study.juli.logging.exception.StudyJuliRuntimeException;
import org.study.juli.logging.filter.Filter;
import org.study.juli.logging.formatter.Formatter;
import org.study.juli.logging.handler.AbstractHandler;
import org.study.juli.logging.handler.Handler;
import org.study.juli.logging.queue.StudyHandler;
import org.study.juli.logging.utils.ClassLoadingUtils;
import org.study.juli.logging.worker.ProducerNoticeConsumerWorker;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-04 14:07
 * @since 2021-04-04 14:07:00
 */
public class KafkaHandler extends AbstractHandler {
  /** 生产通知消费处理器.为Handler自己的队列创建一个生产者通知消费者处理程序. */
  private final StudyHandler<Handler> producerNoticeConsumerWorker =
      new ProducerNoticeConsumerWorker();
  /** . */
  private final Runnable consumerRunnable = createConsumerRunnable();
  /** . */
  private KafkaQueue kafkaQueue;
  /** 生产日志处理器. */
  protected StudyHandler<LogRecord> producerWorker;

  private String brokerList;
  private String topic;
  private String compressionType;
  private String securityProtocol;
  private String sslTruststoreLocation;
  private String sslTruststorePassword;
  private String sslKeystoreType;
  private String sslKeystoreLocation;
  private String sslKeystorePassword;
  private String saslKerberosServiceName;
  private String saslMechanism;
  private String clientJaasConfPath;
  private String clientJaasConf;
  private String kerb5ConfPath;
  private Integer maxBlockMs;
  private String sslEngineFactoryClass;
  private int lingerMs;
  private Producer<String, String> producer;

  /**
   * This is a method description.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public KafkaHandler() {
    try {
      // 读取日志配置文件,初始化配置.
      config();
      // 动态配置队列属性.
      kafkaQueue = new KafkaQueue(topic);
      producerWorker = kafkaQueue.createProducerWorker();
      // 开始创建文件流,用于日志写入.
      open();
    } catch (Exception e) {
      // 处理所有异常.
      throw new StudyJuliRuntimeException(e);
    }
  }

  /**
   * 每向队列中产生一条日志,会触发flush这个方法.
   *
   * <p>关于读写锁,参考JDK ReentrantReadWriteLock第137行例子.
   *
   * @author admin
   */
  @Override
  public void flush() {
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    String current = intervalFormatter.format(zdt);
    // 首先加一个读锁.
    readLock.lock();
    try {
      long currentLong = Long.parseLong(current);
      // 检查当前日期和上一次的日期.如果不相等,需要重新打开一个新的日志文件.
      if (checkState(currentLong)) {
        // 释放读锁.
        readLock.unlock();
        // 加一个写锁.
        writeLock.lock();
        try {
          // 重新检查状态.
          if (checkState(currentLong)) {
            // 关闭流之前,消费掉队列里面的全部数据.
            process(Constants.BATCH_SIZE);
            // 关闭原来的文件流.
            closeIo();
            // 改变日志interval标志.
            initialization = currentLong;
            // 重新打开新的的文件流.
            open();
          }
        } finally {
          // 加一个读锁.
          readLock.lock();
          // 释放写锁.
          writeLock.unlock();
        }
      }
      // 具体业务逻辑.
      LOG_CONSUMER_CONTEXT.executeInExecutorService(consumerRunnable);
    } finally {
      // 释放读锁.
      readLock.unlock();
    }
  }

  /**
   * JDK会调用这个方法.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  @Override
  public void publish(final LogRecord record) {
    // 记录当前处理器最后一次处理日志的时间.
    sys = System.currentTimeMillis();
    GLOBAL_COUNTER.incrementAndGet();
    counter.incrementAndGet();
    // 得到当前处理器的日志级别.
    Level level = this.getLevel();
    // 处理器可以处理日志的级别.
    final int levelValue = level.intValue();
    // 用户发送日志的级别.
    int recordLevel = record.getLevel().intValue();
    // 如果日志的消息级别,比当前处理器的级别小则不处理日志. 如果当前处理器关闭日志级别,处理器也不处理日志.
    if (recordLevel < levelValue || levelValue == Level.OFF.intValue()) {
      return;
    }
    // 获取当前处理器的日志过滤器.
    Filter filter = this.getFilter();
    // 如果过滤器返回false,当前日志消息丢弃.
    if (!filter.isLoggable(record)) {
      return;
    }
    // 启动一个线程,开始生产日志.(考虑将LogRecord预先格式化成字符串消息,LogRecord对象生命周期结束.)
    LOG_PRODUCER_CONTEXT.executeInExecutorService(record, producerWorker);
    // 如果队列容量大于等于5000,通知消费者消费.如果此时生产者不再生产数据,则队列中会有<5000条数据永久存在,因此需要启动一个守护者线程GUARDIAN处理.
    int size = kafkaQueue.size();
    // 当前处理器的队列中日志消息达到5000条,处理一次.
    if (size >= Constants.BATCH_SIZE) {
      // 提交一个任务,用于通知消费者线程去消费队列数据.
      LOG_PRODUCER_NOTICE_CONSUMER_CONTEXT.executeInExecutorService(
          this, producerNoticeConsumerWorker);
    }
  }

  /**
   * 读写锁状态检查,日志文件按照日期进行切换的条件.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  private boolean checkState(final long current) {
    // 文件翻转开关打开并且当前系统时间减去初始化的时间大于间隔时间,即可进行翻转日志文件.
    return rotatable && (current - initialization) >= interval;
  }

  /**
   * 配置方法.
   *
   * <p>Another description after blank line.
   *
   * @throws Exception 抛出所有异常.
   * @author admin
   */
  private void config() throws Exception {
    // 是否按照天,进行切分日志.
    String rotatable = getProperty(".rotatable", "true");
    // 日志文件翻转开关.
    this.rotatable = Boolean.parseBoolean(rotatable);
    // 设置日志文件翻转间隔.
    interval = Integer.parseInt(getProperty(".interval", "1"));
    // 设置日志文件翻转间隔格式化.
    intervalFormatter = DateTimeFormatter.ofPattern(getProperty(".intervalFormatter", "yyyyMMdd"));
    // UTC时区获取当前系统的日期.
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    // 设置处理器创建时当前的系统时间.
    initialization = Long.parseLong(intervalFormatter.format(zdt));
    // 设置处理器创建时当前的系统时间.
    // 设置日志文件的编码.
    setEncoding(getProperty(".encoding", "UTF-8"));
    // 设置日志文件的级别.
    setLevel(Level.findLevel(getProperty(".level", "" + Level.ALL)));
    // 设置日志文件的过滤器.
    String filterName = getProperty(".filter", "org.study.juli.logging.filter.StudyJuliFilter");
    // 设置过滤器.
    Constructor<?> filterConstructor = ClassLoadingUtils.constructor(filterName);
    setFilter((Filter) filterConstructor.newInstance());
    // 获取日志格式化器.
    String formatterName =
        getProperty(".formatter", "org.study.juli.logging.formatter.StudyJuliMessageFormatter");
    // 设置日志格式化器.
    Constructor<?> formatterConstructor = ClassLoadingUtils.constructor(formatterName);
    setFormatter((Formatter) formatterConstructor.newInstance());
    // 设置日志格式化器.
    formatter = getFormatter();
  }

  /**
   * 创建一个消费者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public ConsumerRunnable createConsumerRunnable() {
    return new ConsumerRunnable();
  }

  /**
   * 消费者线程任务.
   *
   * <p>Another description after blank line.
   *
   * @author admin
   */
  public class ConsumerRunnable implements Runnable {

    /**
     * 在执行业务之前,进行检查.
     *
     * <p>Another description after blank line.
     *
     * @author admin
     */
    @Override
    public void run() {
      // 重新获取队列元素数.
      int size = kafkaQueue.size();
      // 如果队列为空,不执行业务.
      if (size != 0) {
        // 如果元素数大于flushCount(默认100),则每次获取100条.否则直接获取全部元素.
        process(Math.min(size, Constants.FLUSH_COUNT));
      }
    }
  }

  public void process(final int size) {
    try {
      // 获取一批数据,写入磁盘.
      for (int i = 0; i < size; i++) {
        // 非阻塞方法获取队列元素.
        LogRecord logRecord = kafkaQueue.poll();
        // 如果数量不够,导致从队列获取空对象.
        if (logRecord == null) {
          break;
        }
        // 需要加写锁,可能会关闭. idempotence 幂等性.
        String message = formatter.format(logRecord);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, message);
        // 异步发送,提供回调.如果同步发送,直接调用get方法.
        producer.send(producerRecord, new KafkaProducerCallback());
      }
    } catch (Exception e) {
      // ignore Exception.
      throw new StudyJuliRuntimeException(e);
    }
  }

  private void open() {
    writeLock.lock();
    try {
      Properties props = new Properties();
      props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerList);
      props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
      props.put(ProducerConfig.ACKS_CONFIG, Constants.REQUIRED_NUM_ACK_S);
      props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
      props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, Constants.DELIVERY_TIMEOUT_MS);
      props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
      props.put(ProducerConfig.BATCH_SIZE_CONFIG, Constants.BATCH_SIZE);
      props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
      props.put(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG, sslEngineFactoryClass);

      if (securityProtocol.contains("SSL")) {
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, sslKeystoreType);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslKeystoreLocation);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslKeystorePassword);
      }

      if (securityProtocol.contains("SASL")) {
        props.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, saslKerberosServiceName);
        System.setProperty("java.security.auth.login.config", clientJaasConfPath);
      }

      System.setProperty("java.security.krb5.conf", kerb5ConfPath);
      props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
      props.put(SaslConfigs.SASL_JAAS_CONFIG, clientJaasConf);
      props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
      this.producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
    } catch (Exception e) {
      // 如何任何阶段发生了异常,主动关闭所有IO资源.
      closeIo();
      throw new StudyJuliRuntimeException(e);
    } finally {
      writeLock.unlock();
    }
  }

  public void closeIo() {
    writeLock.lock();
    try {
      // 尝试关闭producer.
      if (producer != null) {
        producer.close();
      }
    } finally {
      writeLock.unlock();
    }
  }

  public KafkaQueue getKafkaQueue() {
    return kafkaQueue;
  }
}
