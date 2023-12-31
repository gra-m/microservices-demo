package com.microservices.demo.elastic.query.service.transformer;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ElasticToResponseModelTransformer {

public ElasticQueryServiceResponseModel getResponseModel(TwitterIndexModel twitterIndexModel) {
   return ElasticQueryServiceResponseModel.builder()
   .userId(twitterIndexModel.getUserId())
   .id(twitterIndexModel.getId())
   .text(twitterIndexModel.getText())
   .createdAt(twitterIndexModel.getCreatedAt())
   .build();

}

public List<ElasticQueryServiceResponseModel> getResponseModels(List<TwitterIndexModel> twitterIndexModels) {
   return twitterIndexModels.stream().map(this::getResponseModel).collect(Collectors.toList());
}
}
