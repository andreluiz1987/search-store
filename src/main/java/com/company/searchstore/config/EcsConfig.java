package com.company.searchstore.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EcsConfig {

//    @Value("${elasticsearch.connect-timeout:5000}")
//    private int connectTimeout;
//
//    @Value("${elasticsearch.socket-timeout:5000}")
//    private int socketTimeout;
//
//    @Value("${search.ecs.config.threads.io:0}")
//    private int ioThreads;

  @Bean
  public ElasticsearchClient getEcsClient() {

    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200)
    ).build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
        restClient, new JacksonJsonpMapper());

    // And create the API client
    return new ElasticsearchClient(transport);
  }

//  @Bean(destroyMethod = "close")
//  public RestClient restClient(@Value("${search.ecs.config.url}") String ecsHost) {
//    return getEcsClient(ecsHost).getLowLevelClient();
//  }
//
//  private int getIoThreadCount() {
//    if (this.ioThreads == 0) {
//      return Runtime.getRuntime().availableProcessors();
//    }
//
//    return this.ioThreads;
//  }
}