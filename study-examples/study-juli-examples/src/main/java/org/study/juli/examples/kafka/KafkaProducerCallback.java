package org.study.juli.examples.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * This is a class description.
 *
 * <p>Another description after blank line.
 *
 * @author admin
 * @version 2021-04-04 16:00
 * @since 2021-04-04 16:00:00
 */
public class KafkaProducerCallback implements Callback {

  @Override
  public void onCompletion(RecordMetadata recordMetadata, Exception e) {
    //
  }
}
