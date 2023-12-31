package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

@Component
//@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {
private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);
private static final Random RANDOM = new Random();
private static final String[] WORDS = new String[] {
"She",
"he",
"it",
"went",
"thought",
"threw",
"light",
"manners",
"no",
"yes",
"thoughtfully",
"underwent",
"considered",
"amazing",
"intrepid",
"antiseptic",
"amount",
"amiable",
"feral",
"agnostic",
"excellent",
"fantastic",
"content",
"agile",
"smooth",
"fast"
};
private static final String tweetAsRawJson = "{" +
"\"created_at\":\"{0}\"," +
"\"id\":\"{1}\"," +
"\"text\":\"{2}\"," +
"\"user\":{\"id\":\"{3}\"}" +
"}";
private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
private final TwitterKafkaStatusListener twitterKafkaStatusListener;

public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
                             TwitterKafkaStatusListener twitterKafkaStatusListener) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.twitterKafkaStatusListener = twitterKafkaStatusListener;
}

private static String formatStringAsJsonWithParams(String[] params) {
    String tweet = tweetAsRawJson;

    for ( int i = 0; i < params.length; i++) {
        tweet = tweet.replace("{" + i + "}", params[i]);
    }
    return tweet;
}

private static String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
    for ( int i = 0; i < tweetLength; i++) {
        tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
       if (i == tweetLength / 2) {
            tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            LOG.info("Added keyword");
        }
    }
    return tweet.toString().trim();
}

/**
 * @throws TwitterException
 */
@Override
public void start() throws TwitterException {
    String[] keywords =  twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
    int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
    long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();

    LOG.info("Starting to mock Twitter Stream for keywords {}", Arrays.toString(keywords));
    simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);


}

/** INFO:
 * Simulated Twitter Stream running in a separate thread so as not to block the main thread, the lambda within the
 * submit method of Executors.newSingleThreadExecutor actually implements the Runnable interface.
 *
 * @param keywords words to be searched for
 * @param minTweetLength min
 * @param maxTweetLength max
 * @param sleepTimeMs pause between 'tweets'
 */
private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
    Executors.newSingleThreadExecutor().submit(()->{
        try {
            while (true) {
                String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                twitterKafkaStatusListener.onStatus(status);
                sleep(sleepTimeMs);
            }
        }
        catch( TwitterException e ) {
            LOG.error("Error creating twitter status!", e);
        }
    });
}

private void sleep(long sleepTimeMs) {
    try {
        Thread.sleep(sleepTimeMs);
    }
    catch( InterruptedException e ) {
        throw new TwitterToKafkaServiceException("Error while sleeping between status' being created in " +
        "MockStreamRunner", e);
    }
}

private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
    String [] params =
    new String[] {ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
    String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
    getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
    String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
    };

    return formatStringAsJsonWithParams(params);
}

private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
    StringBuilder tweet = new StringBuilder();
    int tweetLength = RANDOM.nextInt((maxTweetLength - minTweetLength) + 1) + minTweetLength;
    LOG.info("Initial random tweet length was {}, this will change with addition of keywords..", tweetLength);
    return constructRandomTweet(keywords, tweet, tweetLength);
}


}
