package com.company.searchstore.core;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.company.searchstore.models.Movie;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCoreService {

  @Value("${es.indexName}")
  private String index;
  private final ElasticsearchClient client;

  public SearchResponse<Movie> getAll(int size, int from) throws IOException {
    var offset = from * size;
    var response = client.search(SearchRequest.of(
        sr -> sr.index(index)
            .size(size)
            .from(offset)
            .query(Query.of(q -> q.matchAll(MatchAllQuery.of(ma -> ma)))))
        , Movie.class);
    return response;
  }
}
