package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-config")
public class ElasticConfigData {
  /** [M] config-server-repository config-client-kafka_to_elastic.yml */
  private String indexName;

  private String connectionUrl;
  private Integer connectTimeoutMs;
  private Integer socketTimeoutMs;
}
