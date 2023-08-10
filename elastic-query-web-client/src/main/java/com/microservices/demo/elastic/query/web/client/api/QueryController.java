package com.microservices.demo.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import com.microservices.demo.elastic.query.web.client.service.impl.TwitterElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Cannot use @RestController here as it auto converts to json, instead @Controller. This is the gateway api into the
 * web application
 */
@Controller
public class QueryController {
private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

private final ElasticQueryWebClient elasticQueryWebClient;

public QueryController(ElasticQueryWebClient webClient) {
    this.elasticQueryWebClient = webClient;
}


@GetMapping("")
public String index() {
    return "index";
}

@GetMapping("/error")
public String error() {
    return "error";
}

  @GetMapping("/home")
  public String home(Model model) {
    model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
    return "home";
}

@PostMapping("/query-by-text")
public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {

    LOG.info("Querying with text: {}", requestModel.getText());

    //Now model updated with real data..
    List<ElasticQueryWebClientResponseModel> responseModels = elasticQueryWebClient.getDataByText(requestModel);

    model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
    model.addAttribute("searchText", requestModel.getText());
    //model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
    model.addAttribute("elasticQueryWebClientRequestModel", requestModel);  // keeps search term on screen

    return "home";



}

}
