package com.company.searchstore.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EcsConfig {

  @Bean
  public ElasticsearchClient getEcsClient(@Value("${es.url}") String ecsHost) {
    RestClient restClient = RestClient.builder(HttpHost.create(ecsHost)).build();
    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
    return new ElasticsearchClient(transport);
  }
}