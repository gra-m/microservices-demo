package com.microservices.demo.common.config;

import com.microservices.demo.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {
  private RetryConfigData retryConfigData;

  public RetryConfig(RetryConfigData retryConfigData) {
    this.retryConfigData = retryConfigData;
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();
    ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
    SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();

    exponentialBackOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
    exponentialBackOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
    exponentialBackOffPolicy.setMultiplier(retryConfigData.getMultiplier());
    retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

    simpleRetryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());
    retryTemplate.setRetryPolicy(simpleRetryPolicy);

    return retryTemplate;
  }
}
