package com.company.searchstore;


import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.company.searchstore.core.SearchCoreService;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.mappers.MovieMapper;
import com.company.searchstore.models.Movie;
import com.company.searchstore.services.SearchService;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "es.indexName=idx_movies_suggest")
@Testcontainers
public class SearchCoreServiceTest extends ElasticsearchInitializer {

  public SearchService service;

  @Override
  public void afterMount() {
    var coreService = new SearchCoreService(client);
    service = new SearchService(Mappers.getMapper(MovieMapper.class), coreService);
    ReflectionTestUtils.setField(coreService, "index", "idx_movies_suggest");
  }

  @Test
  public void testIndexWasCreated() throws IOException {
    var response = client.cat().indices();
    var indexes = response.valueBody();
    Assertions.assertTrue(indexes.stream().anyMatch(i -> {
      assert i.index() != null;
      return i.index().equals("idx_movies_suggest");
    }));
  }

  @Test
  public void testTotalDocumentsIsEqual6() throws IOException {
    var response = client.search(SearchRequest.of(s -> s.query(Query.of(q -> q.matchAll(MatchAllQuery.of(ma -> ma))))), Movie.class);
    Assertions.assertEquals(6, response.hits().hits().size());
  }

  @Test
  public void a() throws IOException {
    var dto = new SearchDTO();
    dto.setText("pulp");
    var response = service.getSuggestions(dto);
    Assertions.assertNotNull(response);
  }

  @Test
  public void b() throws IOException {
    var dto = new SearchDTO();
    dto.setText("pulp");
    var response = service.search(dto);
    Assertions.assertNotNull(response);
  }
}
