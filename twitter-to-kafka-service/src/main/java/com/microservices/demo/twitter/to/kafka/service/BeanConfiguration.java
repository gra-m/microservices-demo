package com.microservices.demo.twitter.to.kafka.service;

import config.TwitterToKafkaServiceConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

@Bean
public TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData(){
    return new TwitterToKafkaServiceConfigData();
}
}
