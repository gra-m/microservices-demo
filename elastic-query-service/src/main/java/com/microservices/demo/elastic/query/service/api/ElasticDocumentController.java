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
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
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
  @GetMapping("/")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {

    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
    LOG.info("Elasticsearch returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(
      @NotEmpty @PathVariable String id) {

    ElasticQueryServiceResponseModel responseModel = elasticQueryService.getDocumentById(id);
    LOG.debug("Elasticsearch returned document with id {} ", id);

    ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(responseModel);

    return ResponseEntity.ok(responseModelV2);
  }

  @GetMapping("/{id}")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(
      @NotEmpty @PathVariable String id) {

    ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
    LOG.debug("Elasticsearch returned document with id {} ", id);

    return ResponseEntity.ok(response);
  }

  /**
   * @param elasticQueryServiceRequestModel the json automatically serialized into a request model
   * @return
   */
  @PostMapping("/get-document-by-text")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
      @Valid @RequestBody ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {

    List<ElasticQueryServiceResponseModel> response =
        elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText());
    LOG.debug("Elastic search returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }

  //////////////////// Version Conversion

  private ElasticQueryServiceResponseModelV2 getV2Model(
      ElasticQueryServiceResponseModel elasticQueryServiceResponseModel) {

    ElasticQueryServiceResponseModelV2 responseModelV2 =
        ElasticQueryServiceResponseModelV2.builder()
            .id(Long.parseLong(elasticQueryServiceResponseModel.getId()))
            .text(elasticQueryServiceResponseModel.getText())
            .text2("Version 2 text")
            .userId(elasticQueryServiceResponseModel.getUserId())
            .build();

    responseModelV2.add(elasticQueryServiceResponseModel.getLinks());

    return responseModelV2;
  }
}
