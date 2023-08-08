package com.microservices.demo.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

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
  private Long userId;
  private LocalDateTime createdAt;
}
