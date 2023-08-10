package com.microservices.demo.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {
private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

private final WebClient.Builder webClientBuilder;

public TwitterElasticQueryWebClient(WebClient.Builder webBuilder,
                                    ElasticQueryWebClientConfigData webConfig) {
    this.webClientBuilder = webBuilder;
    this.elasticQueryWebClientConfigData = webConfig;
}

private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

@Override
public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
    return null;
}


}
