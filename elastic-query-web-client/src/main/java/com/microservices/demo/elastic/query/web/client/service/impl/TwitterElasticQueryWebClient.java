package com.microservices.demo.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.elastic.query.web.client.exception.ElasticQueryWebClientException;
import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {
private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

private final WebClient.Builder webClientBuilder;
private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

/**
 * @Qualifier ties back to our bespoke implementation of webbuilder.
 * @param webBuilder
 * @param webConfig
 */
public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder")WebClient.Builder webBuilder,
                                    ElasticQueryWebClientConfigData webConfig) {
    this.webClientBuilder = webBuilder;
    this.elasticQueryWebClientConfigData = webConfig;
}

/**
 * Block allows this to be returned straight away for testing purposes, normally would be asynch with CallBacks..
 * @param requestModel
 * @return
 */
@Override
public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
    LOG.info("Querying by text {}", requestModel.getText());
    return getWebClient(requestModel)
    .bodyToFlux(ElasticQueryWebClientResponseModel.class)
    .collectList()
    .block();
}

private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
    return webClientBuilder
    .build()
    .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
    .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
    .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
    .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
    .retrieve()
    .onStatus(
    httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
    clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
    .onStatus(
    HttpStatus::is4xxClientError,
    cr -> Mono.just(new ElasticQueryWebClientException(cr.statusCode().getReasonPhrase())))
    .onStatus(
    HttpStatus::is4xxClientError,
              cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())));
}

private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
    return new ParameterizedTypeReference<T>() {};
}


}
