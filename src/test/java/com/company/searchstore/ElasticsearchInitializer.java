package com.company.searchstore;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.company.searchstore.models.Movie;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.io.ByteStreams;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ElasticsearchInitializer {

  @Container
  public static final ElasticsearchContainer ELASTIC_CONTAINER = new ElasticsearchContainer(
      DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch").withTag("7.17.4"))
      .withReuse(true);

  protected ElasticsearchClient client;

  @BeforeAll
  public void init() throws IOException, InterruptedException {
    var httpHostAddress = ELASTIC_CONTAINER.getHttpHostAddress();
    var restClient = RestClient.builder(HttpHost.create(httpHostAddress)).build();
    var transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
    client = new ElasticsearchClient(transport);
    createIndex();
    loadData();
    afterMount();
  }

  public abstract void afterMount();

  private void createIndex() {
    try {
      var in = getClass().getClassLoader().getResourceAsStream("mapping.json");
      var mapping = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
      var stringReader = new StringReader(mapping);
      var request = CreateIndexRequest.of(builder -> builder.index("idx_movies_suggest").withJson(stringReader));
      var response = client.indices().create(request);
      log.info("Index Created: {}", response.acknowledged());
    } catch (IOException e) {
      throw new IllegalStateException("Impossible create index", e);
    }
  }

  private void loadData() throws IOException, InterruptedException {
    var in = getClass().getClassLoader().getResourceAsStream("dataset.json");
    var dataset = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);

    var objectMapper = new ObjectMapper();
    var movies = objectMapper.readValue(dataset, new TypeReference<List<Movie>>() {
    });

    BulkRequest.Builder br = new BulkRequest.Builder();
    movies.forEach(movie -> {
      br.operations(op -> op
          .index(idx -> idx
              .index("idx_movies_suggest")
              .document(movie)
          )
      );
    });

    var response = client.bulk(br.build());
    log.info("Erros: {}", response.errors());
    Thread.sleep(2000L);
  }
}
