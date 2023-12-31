package com.microservices.demo.kafka.producer.service.impl;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.service.KafkaProducer;
import java.util.Objects;
import javax.annotation.PreDestroy;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);
  private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

  public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  private void addCallback(
      String topicName,
      TwitterAvroModel message,
      ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
    kafkaResultFuture.addCallback(
        new ListenableFutureCallback<>() {
          @Override
          public void onFailure(Throwable ex) {
            LOG.debug(
                "Error while sending message {} to topic {}", message.getText(), topicName, ex);
          }

          @Override
          public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
            RecordMetadata metadata = result.getRecordMetadata();
            LOG.debug(
                "Received new metadata. Topic: {}; Partition: {}; offset {}; Timestamp {}, at time {}",
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                metadata.timestamp(),
                System.nanoTime());
          }
        });
  }

  /**
   * @param topicName
   * @param key
   * @param message
   */
  @Override
  public void send(String topicName, Long key, TwitterAvroModel message) {
    LOG.info("Sending message = '{}' to topic '{}'", message, topicName);
    ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
        kafkaTemplate.send(topicName, key, message);

    addCallback(topicName, message, kafkaResultFuture);
  }

  @PreDestroy
  public void close() {
    if (Objects.nonNull(kafkaTemplate)) LOG.info("Destroying kafka template!");
    kafkaTemplate.destroy();
  }
}
