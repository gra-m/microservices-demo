package com.microservices.demo.elastic.query.service.api.error.handler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class ElasticQueryServiceErrorHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticQueryServiceErrorHandler.class);

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handle(AccessDeniedException e) {
    LOG.error("Access denied exception!", e);

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("You are not authorised to access this resource!");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handle(IllegalArgumentException e) {
    LOG.error("Illegal argument exception!", e);

    return ResponseEntity.badRequest()
        .body(String.format("Illegal argument exception! \n%s", e.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handle(RuntimeException e) {
    LOG.error("Service runtime excepton!", e);

    return ResponseEntity.badRequest()
        .body(String.format("Service runtime exception! \n%s", e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handle(Exception e) {
    LOG.error("Internal server error!", e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A server error occurred!");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException e) {
    LOG.error("Method argument validation exception", e);

    Map<String, String> errors = new HashMap<>();

    e.getBindingResult()
        .getAllErrors()
        .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));

    return ResponseEntity.badRequest().body(errors);
  }
}
