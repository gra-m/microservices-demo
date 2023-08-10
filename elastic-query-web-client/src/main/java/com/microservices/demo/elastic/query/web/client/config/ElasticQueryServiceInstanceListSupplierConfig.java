package com.microservices.demo.elastic.query.web.client.config;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @Primary to explicitly override Springs built in option
 */
@Primary
@Configuration
public class ElasticQueryServiceInstanceListSupplierConfig implements ServiceInstanceListSupplier {

// Webclient == inner configuration object of ElasticQueryWebClientConfigData...
private final ElasticQueryWebClientConfigData.WebClient webClientConfig;

public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryWebClientConfigData webClientConfigData) {

    this.webClientConfig = webClientConfigData.getWebClient();
}

@Override
public String getServiceId() {
    return null;
}

@Override
public Flux<List<ServiceInstance>> get() {
    return null;
}


}
