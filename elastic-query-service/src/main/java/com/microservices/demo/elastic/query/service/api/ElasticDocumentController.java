package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @ RestController negates need of a @Response annotation
 */
@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

  /**
   * Automatic json serialization with @ResponseBody, but not explicitly required
   * because @RestController handles this.
   *
   * <p>mapping / means this will be automaitically called with the base path.
   */
  @GetMapping("/")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
    List<ElasticQueryServiceResponseModel> response = new ArrayList<>();

    LOG.info("Elasticsearch returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(
      @PathVariable String id) {
    ElasticQueryServiceResponseModel elasticQueryServiceResponseModel =
        ElasticQueryServiceResponseModel.builder().id(id).build();

    LOG.debug("Elasticsearch returned document with id {} ", id);

    return ResponseEntity.ok(elasticQueryServiceResponseModel);
  }

  /**
   * @param elasticQueryServiceRequestModel the json automatically serialized into a request model
   * @return
   */
  @PostMapping("/get-document-by-text")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
      @RequestBody ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {

    List<ElasticQueryServiceResponseModel> response = new ArrayList<>();

    ElasticQueryServiceResponseModel elasticQueryServiceResponseModel =
        ElasticQueryServiceResponseModel.builder()
            .text(elasticQueryServiceRequestModel.getText())
            .build();
    response.add(elasticQueryServiceResponseModel);

    LOG.debug("Elastic search returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }
}
