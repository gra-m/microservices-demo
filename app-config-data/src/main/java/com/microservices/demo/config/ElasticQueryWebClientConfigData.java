package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-web-client")
public class ElasticQueryWebClientConfigData {
  // sub prefix of elastic-query-web-client (look at config-client-elastic_query_web.yml
  private WebClient webClient;
  private Query queryByText;

  // sub-sub prefixes of elastic-query-web-client
  @Data
  public static class WebClient {
    private Integer connectTimeoutMs;
    private Integer readTimeoutMs;
    private Integer writeTimeoutMs;
    private Integer maxInMemorySize;
    private String contentType;
    private String acceptType;
    private String baseUrl;
    private String serviceId;
    private List<Instance> instances;
  }

  @Data
  public static class Query {
    private String method;
    private String accept;
    private String uri;
  }

  @Data
  public static class Instance {
    private String id;
    private String host;
    private Integer port;
  }
}
