package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  @Operation(summary = "Get all elastic documents.")
  @ApiResponses(value ={
  @ApiResponse(responseCode = "200", description = "Successful response",
  content = {
  @Content(
  mediaType = "application/vnd.api.v1+json",
  schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
  )
  }),
  @ApiResponse(responseCode = "400", description = "Not found."),
  @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {

    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
    LOG.info("Elasticsearch returned {} documents", response.size());

    return ResponseEntity.ok(response);
  }


@Operation(summary = "Get elastic document by id.")
@ApiResponses(value ={
@ApiResponse(responseCode = "200", description = "Successful response",
content = {
@Content(
mediaType = "application/vnd.api.v2+json",
schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
)
}),
@ApiResponse(responseCode = "400", description = "Not found."),
@ApiResponse(responseCode = "500", description = "Internal server error")
})
  @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(
      @NotEmpty @PathVariable String id) {

    ElasticQueryServiceResponseModel responseModel = elasticQueryService.getDocumentById(id);
    LOG.debug("Elasticsearch returned document with id {} ", id);

    ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(responseModel);

    return ResponseEntity.ok(responseModelV2);
  }



@Operation(summary = "Get elastic document by id.")
@ApiResponses(value ={
@ApiResponse(responseCode = "200", description = "Successful response",
content = {
@Content(
mediaType = "application/vnd.api.v1+json",
schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
)
}),
@ApiResponse(responseCode = "400", description = "Not found."),
@ApiResponse(responseCode = "500", description = "Internal server error")
})
  @GetMapping("/{id}")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(
      @NotEmpty @PathVariable String id) {

    ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
    LOG.debug("Elasticsearch returned document with id {} ", id);

    return ResponseEntity.ok(response);
  }


@Operation(summary = "Get elastic document by text.")
@ApiResponses(value ={
@ApiResponse(responseCode = "200", description = "Successful response",
content = {
@Content(
mediaType = "application/vnd.api.v1+json",
schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
)
}),
@ApiResponse(responseCode = "400", description = "Not found."),
@ApiResponse(responseCode = "500", description = "Internal server error")
})
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
