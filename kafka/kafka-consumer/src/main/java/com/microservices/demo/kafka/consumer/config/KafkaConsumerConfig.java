package com.microservices.demo.kafka.consumer.config;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaConsumerConfigData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * If KafkaConsumerConfigData is the bridge to Java template for the yml config values held in
 * config-client-kafka_to_elastic this class makes the configuration values into use able
 * kafka.core/config Spring Beans.
 *
 * <p>ConsumerFactory is configured via KafkaConfigData and KafkaConsumerConfigData and then itself
 * used to configure KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K, V>>
 *
 * @param <K> All kafka data is Serialized, so whatever form the key takes it must be Serializable
 * @param <V> As Avro is being used SpecificRecordBase is an Avro abstract class extended by Avro
 *     Generated Models that we make templates for.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig<K extends Serializable, V extends SpecificRecordBase> {
  private final KafkaConfigData kafkaConfigData;
  private final KafkaConsumerConfigData kafkaConsumerConfigData;

  public KafkaConsumerConfig(
      KafkaConfigData configData, KafkaConsumerConfigData consumerConfigData) {
    this.kafkaConfigData = configData;
    this.kafkaConsumerConfigData = consumerConfigData;
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    props.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfigData.getKeyDeserializer());
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        kafkaConsumerConfigData.getValueDeserializer());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerConfigData.getConsumerGroupId());
    props.put(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerConfigData.getAutoOffsetReset());
    props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
    props.put(
        kafkaConsumerConfigData.getSpecificAvroReaderKey(),
        kafkaConsumerConfigData.getSpecificAvroReader());
    props.put(
        ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerConfigData.getSessionTimeoutMs());
    props.put(
        ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
        kafkaConsumerConfigData.getHeartbeatIntervalMs());
    props.put(
        ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerConfigData.getMaxPollIntervalMs());
    props.put(
        ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
        kafkaConsumerConfigData.getMaxPartitionFetchBytesDefault()
            * kafkaConsumerConfigData.getMaxPartitionFetchBytesBoostFactor());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerConfigData.getMaxPollRecords());
    return props;
  }

  @Bean
  public ConsumerFactory<K, V> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  /**
   * concurrency level == number of threads used by Spring to get data
   *
   * @return
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K, V>>
      kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<K, V> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory());
    factory.setBatchListener(kafkaConsumerConfigData.getBatchListener());
    factory.setConcurrency(kafkaConsumerConfigData.getConcurrencyLevel());
    factory.setAutoStartup(kafkaConsumerConfigData.getAutoStartup());
    factory.getContainerProperties().setPollTimeout(kafkaConsumerConfigData.getPollTimeoutMs());

    return factory;
  }
}
