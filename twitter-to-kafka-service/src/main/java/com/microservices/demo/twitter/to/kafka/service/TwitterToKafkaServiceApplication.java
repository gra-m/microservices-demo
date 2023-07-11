package com.microservices.demo.twitter.to.kafka.service;
import config.TwitterToKafkaServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;


/**
 * Initialization Logic options : @PostConstruct with @Scope("request") would create a new bean for each request
 * rather than the usual SpringBean singleton:
 *
 * @Scope("request")
 * public class..
 *
 * @PostConstruct
 * public void init() {}
 *
 * Application Listener &  @EventListener were also options but CommandlineRunner is used here:
 */
@SpringBootApplication(scanBasePackages = "com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData) {
    this.twitterToKafkaServiceConfigData = configData;
}


public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);}


/**
 * starts microservice and confirms with print to console
 * @param args  none used at present
 */
@Override
public void run(String... args) throws Exception {
    LOG.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
    LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
    
}

}
