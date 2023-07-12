package com.microservices.demo.twitter.to.kafka.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// Contrary to vid7 I had to add separate Bean Configuration, otherwise this class was not being pulled in.
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
