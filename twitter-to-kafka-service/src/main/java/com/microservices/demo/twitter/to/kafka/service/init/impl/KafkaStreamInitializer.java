package com.microservices.demo.twitter.to.kafka.service.init.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class KafkaStreamInitializer implements StreamInitializer {
private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);

private final KafkaAdminClient kafkaAdminClient;
private final KafkaConfigData kafkaConfigData;

public KafkaStreamInitializer(KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData) {
    this.kafkaAdminClient = kafkaAdminClient;
    this.kafkaConfigData = kafkaConfigData;
}

/**
 * 1. use admin client to check operation prior to app start
 *
 */
@Override
public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    LOG.info("Topic(s) named {} ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());

}


}
