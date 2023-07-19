package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.*;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Objects;
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false")
public class TwitterKafkaStreamRunner implements StreamRunner {
private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);
private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
private final TwitterKafkaStatusListener    twitterKafkaStatusListener;
private TwitterStream twitterStream;

/**
 * Remember beans are singletons by default @Prototype to create new  instance everytime object injected.
 * @param twitterToKafkaServiceConfigData     source of keywords for filter
 * @param twitterKafkaStatusListener          extends StatusAdapter which in turn implements StatusListener
 */
public TwitterKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
                                TwitterKafkaStatusListener twitterKafkaStatusListener) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.twitterKafkaStatusListener = twitterKafkaStatusListener;
}

/**
 * @throws TwitterException
 */
@Override
public void start() throws TwitterException {
    twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(twitterKafkaStatusListener);
    addFilter();
}

/** NOTE ONLY:
 * @PreDestroy ensures TwitterStream is closed prior to the application being closed. Note TwitterStream is not a
 * singleton here.
 * Mentioned only as @PreDestroy does not work with @Prototype beans (non-singleton).
 */
@PreDestroy
public void shutdown() {
    if( Objects.nonNull(twitterStream)) {
        LOG.info("Closing twitter stream!");
        twitterStream.shutdown();
    }

}

private void addFilter() {
    String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    twitterStream.filter(new FilterQuery(keywords));
    LOG.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
}


}
