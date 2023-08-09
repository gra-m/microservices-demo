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
public class ElasticQueryServiceResponseModelV2 extends RepresentationModel<ElasticQueryServiceResponseModelV2> {
  private Long id;
  private Long userId;
  private String text;
  private String text2;
}
