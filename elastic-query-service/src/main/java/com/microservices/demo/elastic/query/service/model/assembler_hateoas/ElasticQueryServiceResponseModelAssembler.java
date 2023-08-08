package com.microservices.demo.elastic.query.service.model.assembler_hateoas;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.api.ElasticDocumentController;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.transformer.ElasticToResponseModelTransformer;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ElasticQueryServiceResponseModelAssembler
    extends RepresentationModelAssemblerSupport<
        TwitterIndexModel, ElasticQueryServiceResponseModel> {
    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

/**
 * Assembler gets the tools it needs for HATEOAS - a controller and its resource
 * @param transformer
 */
public ElasticQueryServiceResponseModelAssembler(ElasticToResponseModelTransformer transformer) {
    super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class);
    this.elasticToResponseModelTransformer = transformer;
}

/**
 * Adding a self link and a link to the base path /documents (all documents) to ElasticQueryServiceResponseModel
 * @param twitterIndexModel
 * @return
 */
@Override
public ElasticQueryServiceResponseModel toModel(TwitterIndexModel twitterIndexModel) {
    ElasticQueryServiceResponseModel responseModel =
    elasticToResponseModelTransformer.getResponseModel(twitterIndexModel);

    responseModel.add(
    linkTo(methodOn(ElasticDocumentController.class)
    .getDocumentById((twitterIndexModel.getId())))
    .withSelfRel());
    responseModel.add(
    linkTo(ElasticDocumentController.class)
    .withRel("documents"));

    return responseModel;
}


public List<ElasticQueryServiceResponseModel> toModels(List<TwitterIndexModel> twitterIndexModels) {
    return twitterIndexModels.stream().map(this::toModel).collect(Collectors.toList());
}

}
