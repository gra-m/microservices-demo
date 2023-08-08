package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.common.util.CollectionsUtil;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.repository.TwitterElasticSearchQueryRepository;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/** Marked as primary vs other implementer of ElasticQueryClient - TwitterElasticQueryClient */
@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {
  private static final Logger LOG =
      LoggerFactory.getLogger(TwitterElasticRepositoryQueryClient.class);

  private final TwitterElasticSearchQueryRepository twitterElasticSearchQueryRepository;

  public TwitterElasticRepositoryQueryClient(TwitterElasticSearchQueryRepository repository) {
    this.twitterElasticSearchQueryRepository = repository;
  }

  @Override
  public TwitterIndexModel getIndexModelById(String id) {
    Optional<TwitterIndexModel> gotById = twitterElasticSearchQueryRepository.findById(id);
    LOG.info(
        "Document with id {} retrieved successfully",
        gotById
            .orElseThrow(
                () ->
                    new ElasticQueryClientException(
                        "No document found at elasticsearch with id " + id))
            .getId());

    return gotById.get();
  }

  @Override
  public List<TwitterIndexModel> getIndexModelByText(String text) {
    List<TwitterIndexModel> listGotByText = twitterElasticSearchQueryRepository.findByText(text);

    LOG.info(
        "{} of documents with text {} were retrieved successfully", listGotByText.size(), text);
    return listGotByText;
  }

  @Override
  public List<TwitterIndexModel> getAllIndexModels() {
    List<TwitterIndexModel> searchResult =
        CollectionsUtil.getInstance()
            .getListFromIterable(twitterElasticSearchQueryRepository.findAll());
    LOG.info("{} number of documents retrieved successfully", searchResult.size());
    return searchResult;
  }
}
