package com.microservices.demo.twitter.to.kafka.service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;


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
@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);}


/**
 * starts microservice and confirms with print to console
 * @param args  none used at present
 */
@Override
public void run(String... args) throws Exception {
    System.out.println("Application started...");
}

}
