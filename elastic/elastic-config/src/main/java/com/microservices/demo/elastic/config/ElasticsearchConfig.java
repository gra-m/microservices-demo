package com.microservices.demo.elastic.config;

import com.microservices.demo.config.ElasticConfigData;
import java.util.Objects;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // becomes a spring managed
                                                             // configuration class
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.microservices.demo.elastic.index.client.repository")
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
  private final ElasticConfigData elasticConfigData;

  public ElasticsearchConfig(ElasticConfigData elasticConfigData) {
    this.elasticConfigData = elasticConfigData;
  }

  /**
   * Create a UriComponents object using builder and elasticConfigdata then use it (serverUri) and
   * further elasticConfigdata to build the elasticsearch restClient for use.
   *
   * @return a high-level rest-client for Elasticsearch
   */
  @Override
  @Bean
  public RestHighLevelClient elasticsearchClient() {
    UriComponents serverUri =
        UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl()).build();

    return new RestHighLevelClient(
        RestClient.builder(
                new HttpHost(
                    Objects.requireNonNull(serverUri.getHost()),
                    serverUri.getPort(),
                    serverUri.getScheme()))
            .setRequestConfigCallback(
                requestConfigBuilder ->
                    requestConfigBuilder
                        .setConnectTimeout(elasticConfigData.getConnectTimeoutMs())
                        .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())));
  }

/** For use with Elasticsearch repositories:
 * @EnableElasticsearchRepositories
 *
 * @return ElasticsearchOperations
 */
@Bean
  public ElasticsearchOperations elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(elasticsearchClient());
  }
}
