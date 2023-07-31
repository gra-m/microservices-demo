package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {
  // note the application.yml  twitter-keywords becomes variable twitterKeywords
  private List<String> twitterKeywords;
  private String welcomeMessage;
  private Boolean enableV2Tweets;
  private Boolean enableMockTweets;
  private Integer mockMinTweetLength;
  private Integer mockMaxTweetLength;
  private Long mockSleepMs;
}
