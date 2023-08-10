package com.microservices.demo.elastic.query.service.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * HATEOAS -> RepresentationModel -> extension injects ability to add more to this response model, namely hypermedia
 * links
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponseModel extends RepresentationModel<ElasticQueryServiceResponseModel> {
  private String id;
  private String text;
  // private String text2; Adding field == not a breaking change due to extensibility of json, changing type is.
  private Long userId;
  private LocalDateTime createdAt;
}
