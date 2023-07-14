package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
@Component
public class KafkaAdminClient {
private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);
private final KafkaConfigData kafkaConfigData;
private final RetryConfigData retryConfigData;
private final AdminClient adminClient;
private final RetryTemplate retryTemplate;
private final WebClient webClient;


public KafkaAdminClient(KafkaConfigData kafkaConfigData,
                        RetryConfigData retryConfigData,
                        AdminClient adminClient,
                        RetryTemplate retryTemplate,
                        WebClient webClient) {
    this.kafkaConfigData = kafkaConfigData;
    this.retryConfigData = retryConfigData;
    this.adminClient = adminClient;
    this.retryTemplate = retryTemplate;
    this.webClient = webClient;
}

public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
        createTopicsResult = retryTemplate.execute(retryContext -> doCreateTopics(retryContext));
    }
    catch( Throwable t ) {
        throw new KafkaClientException("Reached max number of retries for creating kafka topic(s)!", t);
    }

    checkTopicsCreated();
}

/**
 *
 * @param retryContext retry context is this.
 * @return
 */
private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
    LOG.info("Creating {} topic(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
    List<NewTopic> kafkaTopics = topicNames.stream().map(
    topic -> new NewTopic(
    topic.trim(),
    kafkaConfigData.getNumOfPartitions(),
    kafkaConfigData.getReplicationFactor())).collect(Collectors.toList());

    return adminClient.createTopics(kafkaTopics);
}

public void checkTopicsCreated() {
    Collection<TopicListing> topics = getTopics();

    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    Integer multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();

    for(String topic : kafkaConfigData.getTopicNamesToCreate()) {
        while(!isTopicCreated(topics, topic)) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
            topics = getTopics();
        }
    }
}

private void sleep(Long sleepTimeMs) {
    try {
        Thread.sleep(sleepTimeMs);
    }
    catch( InterruptedException e ) {
        throw new KafkaClientException(String.format("Thread.sleep (%d) interrupted in KafkaAdminClient", sleepTimeMs),e);
    }
}

/**
 * Note: this is particularly interesting as in my food.ordering.service project the schema is being registered twice
 * for some reason, this involves checking the schema-registry end point (it is running in a kafka node), so maybe some
 * other magic can happen with this knowledge??
 */
public void checkSchemaRegistry() {
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();

    while(!getSchemaRegistryStatus().is2xxSuccessful()) {
        checkMaxRetry(retryCount++, maxRetry);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
    }
    
}

/**
 * NOTE: blocking the operation enables getting result synchronously from the schema registry.
 * @return
 */
private HttpStatus getSchemaRegistryStatus() {
    try {
        return webClient
        .method(HttpMethod.GET)
        .uri(kafkaConfigData.getSchemaRegistryUrl())
        .exchange()
        .map(clientResponse -> clientResponse.statusCode())
        .block();
    }
    catch( Exception e ) {
        LOG.debug("Service unavailable detail ==" + e);
       return HttpStatus.SERVICE_UNAVAILABLE;
    }


}

private void checkMaxRetry(int i, Integer maxRetry) {
    if (i > maxRetry){
        throw new KafkaClientException(String.format("Max retries (%d) exceeded when checking if topic created",
        maxRetry));
    };
}

private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
    if (Objects.isNull(topics)) {
        return false; }

    return topics.stream().anyMatch(topicListing -> topicListing.name().equals(topic));

}

private Collection<TopicListing> getTopics(){
    Collection<TopicListing> topics;
    try {
        topics  = retryTemplate.execute(retryContext -> doGetTopics(retryContext));
    }
    catch( Throwable t ) {
        throw new KafkaClientException( "Reached max number of retries for reading kafka topics!", t );
    }
    return topics;
}

/**
 * First log name of topics that we believe have been created, and the current retrieval attempt, then return created
 * topics from the cluster
 * @param retryContext
 * @return
 */
private Collection<TopicListing> doGetTopics(RetryContext retryContext) {
    LOG.info("Reading kafka topic {}, attempt {}", kafkaConfigData.getTopicNamesToCreate().toArray(),
    retryContext.getRetryCount());
    Collection<TopicListing> topics = null;
    try {
        topics = adminClient.listTopics().listings().get();
        if ( Objects.nonNull(topics)) {
            topics.forEach(topic -> debugLog(topic));
        }
    }
    catch( InterruptedException e ) {
        throw new KafkaClientException("Unable to retrieve topics from adminClient", e);
    }
    catch( ExecutionException e ) {
        throw new KafkaClientException("Unable to retrieve topics from adminClient", e);
    }
    return topics;

}

private void debugLog(TopicListing topic) {
    LOG.debug("Topic with name {}", topic.name());
}


}

