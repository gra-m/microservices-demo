package com.microservices.demo.elastic.index.client.util;

import com.microservices.demo.elastic.model.index.IndexModel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

/**
 * T extends IndexModel defines a Generic upper bound allowing subtypes:
 * ElasticIndexUtil exists to bring T extends IndexModel into existence, meaning its methods can then be used
 * generically by all IndexModels.
 * @param <T> so all manner of IndexModels can be passed, see [M] elastic-model
 */
@Component
public class ElasticIndexUtil<T extends IndexModel> {

  /**
   * Take a list of generic T's that extend IndexModel and convert them into a list of IndexQueries that can be
   * passed to Elasticsearch.
   *
   * NOTE: document.getId() is the reason for adding getId to I IndexModel.
   */
  public List<IndexQuery> getIndexQueries(List<T> documents) {
    return documents.stream()
        .map(
            document ->
                new IndexQueryBuilder().withId(document.getId()).withObject(document).build())
        .collect(Collectors.toList());
  }
}
