package com.microservices.demo.elastic.model.index.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microservices.demo.elastic.model.index.IndexModel;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Spring Expression Language parser class is used to define name of index. @Document points out to SpringFramework..
 * .elasticsearch that this class should be considered as a / is a document candidate.
 * indexName exists in and so is retrieved from ElasticConfigData
 */
@Data
@Builder
@Document(indexName = "#{elasticConfigData.indexName}")
public class TwitterIndexModel implements IndexModel {

private static final String PATTERN = "uuuu-MM-dd'T'HH:mm:ssZZ";

@JsonProperty
private String id;
@JsonProperty
private Long userId;
@JsonProperty
private String text;

/**
 * @Field
 * 1. to convert LocalDateTime to elasticsearch field during index
 * 2. to format the date to custom (X led pattern)
 * 3. to set the field type
 */
@Field(type = FieldType.Date, format = DateFormat.custom, pattern = PATTERN)
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
@JsonProperty
private LocalDateTime createdAt;

@Override
public String getId() {
    return id;
}


}
