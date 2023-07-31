package com.microservices.demo.kafka.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Creating a bean that can be injected anywhere in the application where a rest-call is required ->
 * dependency = spring-boot-starter-webflux
 */
@Configuration
public class WebClientConfig {

  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }
}
