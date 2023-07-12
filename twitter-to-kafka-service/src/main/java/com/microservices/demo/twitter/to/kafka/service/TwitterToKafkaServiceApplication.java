package com.microservices.demo.twitter.to.kafka.service;
import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import com.microservices.demo.twitter.to.kafka.service.runner.impl.TwitterKafkaStreamRunner;
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
private final StreamRunner streamRunner;
private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData,
                                        StreamRunner streamRunner) {
    this.twitterToKafkaServiceConfigData = configData;
    this.streamRunner = streamRunner;
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
    streamRunner.start();
    LOG.info("StreamRunner has started listening to messages.");
    
}

}
