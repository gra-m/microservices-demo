package com.microservices.demo.kafka.producer.config;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaProducerConfigData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * wrapper class around KafkaProducer brings together configurations from KafkaConfigData and
 * KafkaProducerConfigData so that producer is set up to spec at runtime.
 *
 * <p>import org.apache.kafka.clients.producer.KafkaProducer;
 *
 * @param <K> Key Serializable -> becomes e.g. Long in TwitterKafkaProducer
 * @param <V> Value [avro] SpecificRecordBase becomes e.g. TwitterAvroModel in TwitterKafkaProducer
 */
@Configuration
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {
  private final KafkaConfigData kafkaConfigData;
  private final KafkaProducerConfigData kafkaProducerConfigData;

  public KafkaProducerConfig(
      KafkaConfigData kafkaConfigData, KafkaProducerConfigData kafkaProducerConfigData) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaProducerConfigData = kafkaProducerConfigData;
  }

  /**
   * A bean used to get the config details into the map format required by KafkaTemplate to set up
   * producer in Spring Container at runtime
   *
   * @return
   */
  @Bean
  public Map<String, Object> producerConfig() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
    props.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        kafkaProducerConfigData.getKeySerializerClass());
    props.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        kafkaProducerConfigData.getValueSerializerClass());
    props.put(
        ProducerConfig.BATCH_SIZE_CONFIG,
        kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
    props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
    props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
    props.put(
        ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
    props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());
    return props;
  }

  @Bean
  public ProducerFactory<K, V> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
  }

  @Bean
  public KafkaTemplate<K, V> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
