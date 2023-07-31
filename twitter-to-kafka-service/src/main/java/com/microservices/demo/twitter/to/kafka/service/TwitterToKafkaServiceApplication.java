package com.microservices.demo.twitter.to.kafka.service;
import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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
@ComponentScan(basePackages = "com.microservices.demo")
@SpringBootApplication(scanBasePackages = "com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
private final StreamRunner streamRunner;
private final StreamInitializer streamInitializer;
private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

public TwitterToKafkaServiceApplication(StreamRunner streamRunner, StreamInitializer streamInitializer) {
    this.streamRunner = streamRunner;
    this.streamInitializer = streamInitializer;
}

public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);}

/**
 * starts microservice and confirms with print to console
 * @param args  none used at present
 */
@Override
public void run(String... args) throws Exception {
    LOG.info("Twitter->To->Kafka app starts...");
    streamInitializer.init();
    streamRunner.start();

}

}
