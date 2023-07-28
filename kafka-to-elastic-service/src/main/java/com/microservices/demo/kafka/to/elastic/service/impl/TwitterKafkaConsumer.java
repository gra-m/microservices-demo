package com.microservices.demo.kafka.to.elastic.service.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.to.elastic.service.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
 /** Regarding suppressed warning required when trying to constructor inject KafkaListenerEndpointRegistry:
 https://docs.spring.io/spring-kafka/docs/2.3.4.RELEASE/api/org/springframework/kafka/config/KafkaListenerEndpointRegistry.html
 */
@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;

public TwitterKafkaConsumer(KafkaAdminClient kafkaAdminClient,
                            KafkaConfigData kafkaConfigData) {
    this.kafkaListenerEndpointRegistry = new KafkaListenerEndpointRegistry();
    this.kafkaAdminClient = kafkaAdminClient;
    this.kafkaConfigData = kafkaConfigData;
}

/**
 * topics =  pulled from config-client-kafka_to_elastic.yml
 * @param messages
 * @param keys
 * @param partitions
 * @param offsets
 */
@Override
@KafkaListener(id = "twitterTopicListener", topics = "{kafka-config.topic-name}")
public void receive(@Payload List<TwitterAvroModel> messages,
                    @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY)List<Integer> keys,
                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID)List<Integer> partitions,
                    @Header(KafkaHeaders.OFFSET)List<Long> offsets) {
    LOG.info("{} messages received with keys {}, partitions {} and offsets {}. Sending to elastic: Thread id {}",
    messages.size(),
    keys.toString(),
    partitions.toString(),
    offsets.toString(),
    Thread.currentThread().getId());
}


}
