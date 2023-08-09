package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ RestController negates need of a @Response annotation
 */
@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
  private final ElasticQueryService elasticQueryService;

  public ElasticDocumentController(ElasticQueryService queryService) {
    this.elasticQueryService = queryService;
  }

  /**
   * Automatic json serialization with @ResponseBody, but not explicitly required
   * because @RestController handles this.
   *
   * <p>mapping / means this will be automaitically called with the base path.
   */
  @GetMapping("/v1")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {

    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
    LOG.info("Elasticsearch returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/v1/{id}")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(
      @NotEmpty @PathVariable String id) {

    ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
    LOG.debug("Elasticsearch returned document with id {} ", id);

    return ResponseEntity.ok(response);
  }

@GetMapping("/v2/{id}")
public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdv2(
@NotEmpty @PathVariable String id) {

  ElasticQueryServiceResponseModel responseModel = elasticQueryService.getDocumentById(id);
  LOG.debug("Elasticsearch returned document with id {} ", id);

  ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(responseModel);
  return ResponseEntity.ok(responseModelV2);
}

  /**
   * @param elasticQueryServiceRequestModel the json automatically serialized into a request model
   * @return
   */
  @PostMapping("/v1/get-document-by-text")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
      @Valid @RequestBody ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {

    List<ElasticQueryServiceResponseModel> response =
    elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText());
    LOG.debug("Elastic search returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }

  private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel elasticQueryServiceResponseModel) {

    ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
    .id(Long.valueOf(elasticQueryServiceResponseModel.getId()))
    .userId(elasticQueryServiceResponseModel.getUserId())
    .createdAt(elasticQueryServiceResponseModel.getCreatedAt())
    .text(elasticQueryServiceResponseModel.getText()).build();

    responseModelV2.add(elasticQueryServiceResponseModel.getLinks());

    return responseModelV2;
  }
}
