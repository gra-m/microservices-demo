package com.microservices.demo.twitter.to.kafka.service.listener;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

/**
 * Listens to mock tweets, converts them to AvroModel and produces them to Kafka event store.
 */
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {
private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);
private final KafkaConfigData kafkaConfigData;
private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

public TwitterKafkaStatusListener(KafkaConfigData kafkaConfigData,
                                  KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
                                  TwitterStatusToAvroTransformer twitterStatusToAvroTransformer) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaProducer = kafkaProducer;
    this.twitterStatusToAvroTransformer = twitterStatusToAvroTransformer;
}

/**
 * @param status = twitter object
 */
@Override
public void onStatus(Status status) {
    LOG.info("Received status text {} sending to kafka topic {}", status.getText(),
    kafkaConfigData.getTopicName());
    TwitterAvroModel avroModel = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(status);
    // partitioning data by UserId on the kafka topic == each user id gets its own partition
    kafkaProducer.send(kafkaConfigData.getTopicName(), avroModel.getUserId(), avroModel);
}


}
