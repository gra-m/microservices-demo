package com.microservices.demo.kafka.to.elastic.service.consumer.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaConsumerConfigData;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.microservices.demo.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Regarding WORKING suppressed warning required when trying to constructor inject KafkaListenerEndpointRegistry:
 * https://docs.spring.io/spring-kafka/docs/2.3.4.RELEASE/api/org/springframework/kafka/config/KafkaListenerEndpointRegistry.html
 */
@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);
  private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
  private final KafkaAdminClient kafkaAdminClient;
  private final KafkaConfigData kafkaConfigData;
  private final AvroToElasticModelTransformer avroToElasticModelTransformer;
  private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;
  private final KafkaConsumerConfigData kafkaConsumerConfigData;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  public TwitterKafkaConsumer(KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData,
                              KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                              AvroToElasticModelTransformer avroToElasticModelTransformer,
                              ElasticIndexClient<TwitterIndexModel> elasticIndexClient,
                              KafkaConsumerConfigData kafkaConsumerConfigData) {
    this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    this.kafkaAdminClient = kafkaAdminClient;
    this.kafkaConfigData = kafkaConfigData;
    this.avroToElasticModelTransformer = avroToElasticModelTransformer;
    this.elasticIndexClient = elasticIndexClient;
    this.kafkaConsumerConfigData = kafkaConsumerConfigData;
  }

/**
 * @EventListener -> ApplicationStartedEvent
 * with this listener config autostart may be changed back to false: config-client-kafka-to-elastic.yml
 */
@EventListener
public void onAppStarted(ApplicationStartedEvent event) {
  kafkaAdminClient.checkTopicsCreated();
  LOG.info("TwitterKafkaConsumer says \"Topic/s named {}\" is/are ready for operations",
  kafkaConfigData.getTopicNamesToCreate().toArray());
  //Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData
  // .getConsumerGroupId())).start();
  kafkaListenerEndpointRegistry.getListenerContainer("twitterTopicListener").start();
}

/**
   * topics = pulled from config-client-kafka_to_elastic.yml
   *
   * @param messages
   * @param keys
   * @param partitions
   * @param offsets
   */
  @Override
  //@KafkaListener(id = "$kafka-consumer-config.consumer-group-id", topics = "${kafka-config.topic-name}")
  @KafkaListener(id = "twitterTopicListener", topics = "${kafka-config.topic-name}")
  public void receive(
      @Payload List<TwitterAvroModel> messages,
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    //Log received messages
    LOG.info(
        "{} messages received with keys {}, partitions {} and offsets {}. Sending to elastic: Thread id {}",
        messages.size(),
        keys.toString(),
        partitions.toString(),
        offsets.toString(),
        Thread.currentThread().getId());
    //Save received messages to Elasticsearch
    List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
    List<String> documentIds = elasticIndexClient.save(twitterIndexModels);
    LOG.info("From TwitterKafkaConsumer -> Documents saved to elasticsearch with ids {}", documentIds.toArray());
  }
}
