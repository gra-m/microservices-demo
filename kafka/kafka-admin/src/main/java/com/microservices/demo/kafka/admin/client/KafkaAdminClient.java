package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
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
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KafkaAdminClient {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);
  private final KafkaConfigData kafkaConfigData;
  private final RetryConfigData retryConfigData;
  private final AdminClient adminClient;
  private final RetryTemplate retryTemplate;
  private final WebClient webClient;

  public KafkaAdminClient(
      KafkaConfigData config,
      RetryConfigData retryConfigData,
      AdminClient client,
      RetryTemplate template,
      WebClient webClient) {
    this.kafkaConfigData = config;
    this.retryConfigData = retryConfigData;
    this.adminClient = client;
    this.retryTemplate = template;
    this.webClient = webClient;
  }

  public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
      createTopicsResult = retryTemplate.execute(this::doCreateTopics);
      LOG.info("Create topic result {}", createTopicsResult.values().values());
    } catch (Throwable t) {
      throw new KafkaClientException("Reached max number of retry for creating kafka topic(s)!", t);
    }

    checkTopicsCreated();
  }

  public void checkTopicsCreated() {
    Collection<TopicListing> topics = getTopics();

    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();

    for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
      while (!isTopicCreated(topics, topic)) {
        checkMaxRetry(retryCount++, maxRetry);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
        topics = getTopics();
      }
    }
  }

  /**
   * Note: this is particularly interesting as in my food.ordering.service project the schema is
   * being registered twice for some reason, this involves checking the schema-registry end point
   * (it is running in a kafka node), so maybe some other magic can happen with this knowledge??
   */
  public void checkSchemaRegistry() {
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();

    while (!getSchemaRegistryStatus().is2xxSuccessful()) {
      checkMaxRetry(retryCount++, maxRetry);
      sleep(sleepTimeMs);
      sleepTimeMs *= multiplier;
    }
  }

  /**
   * NOTE: blocking the operation enables getting result synchronously from the schema registry.
   *
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
    } catch (Exception e) {
      LOG.debug("Service unavailable detail ==" + e);
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
  }

  private void sleep(Long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new KafkaClientException("Error while sleeping for waiting new created topics!!");
    }
  }

  private void checkMaxRetry(int retry, Integer maxRetry) {
    if (retry > maxRetry) {
      throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
    }
  }

  private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
    if (topics == null) {
      return false;
    }
    return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
  }

  /**
   * @param retryContext retry context is this.
   * @return
   */
  private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
    LOG.info("Creating {} topics(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
    List<NewTopic> kafkaTopics =
        topicNames.stream()
            .map(
                topic ->
                    new NewTopic(
                        topic.trim(),
                        kafkaConfigData.getNumOfPartitions(),
                        kafkaConfigData.getReplicationFactor()))
            .collect(Collectors.toList());

    return adminClient.createTopics(kafkaTopics);
  }

  private Collection<TopicListing> getTopics() {
    Collection<TopicListing> topics;
    try {
      topics = retryTemplate.execute(this::doGetTopics);
    } catch (Throwable t) {
      throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!", t);
    }
    return topics;
  }

/**
 * First log name of topics that we believe have been created, and the current retrieval attempt,
 * then return created topics from the cluster
 *
 * @param retryContext
 * @return
 */
  private Collection<TopicListing> doGetTopics(RetryContext retryContext)
      throws ExecutionException, InterruptedException {
    LOG.info(
        "Reading kafka topic {}, attempt {}",
        kafkaConfigData.getTopicNamesToCreate().toArray(),
        retryContext.getRetryCount());
    Collection<TopicListing> topics = adminClient.listTopics().listings().get();
    if (topics != null) {
      topics.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
    }
    return topics;
  }
}
