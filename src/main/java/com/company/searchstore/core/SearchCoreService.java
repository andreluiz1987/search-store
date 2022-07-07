package com.company.searchstore.core;

import static org.apache.http.util.TextUtils.isEmpty;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest.Builder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.company.searchstore.core.fields.FieldAttr;
import com.company.searchstore.core.fields.FieldAttr.Aggregations;
import com.company.searchstore.core.fields.FieldAttr.Suggest;
import com.company.searchstore.dto.MovieSuggestDTO;
import com.company.searchstore.models.Movie;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCoreService {

  @Value("${es.indexName}")
  private String index;
  private final ElasticsearchClient client;

  public SearchResponse<Movie> searchTerm(String term, int size, List<String> searchAfter) throws IOException {
    SearchRequest searchRequest = SearchRequest.of(s -> {
      s.index(index);
      s.size(size);
      addQuery(s, term);
      addSearchAfter(searchAfter, s);
      addSort(s);
      return s;
    });
    return client.search(searchRequest, Movie.class);
  }

  public SearchResponse<Void> getFacets(String term, List<String> searchAfter) throws IOException {
    Map<String, Aggregation> map = new HashMap<>();

    map.put(Aggregations.FACET_GENRE_NAME, new Aggregation.Builder()
        .terms(new TermsAggregation.Builder().field(Aggregations.FACET_GENRE).size(25).build())
        .build());
    map.put(Aggregations.FACET_CERTIFICATE_NAME, new Aggregation.Builder()
        .terms(new TermsAggregation.Builder().field(Aggregations.FACET_CERTIFICATE).size(10).build())
        .build());
    SearchRequest searchRequest = SearchRequest.of(s -> {
      s.index(index);
      s.size(0);
      addQuery(s, term);
      addSearchAfter(searchAfter, s);
      addSort(s);
      s.aggregations(map);
      return s;
    });
    return client.search(searchRequest, Void.class);
  }

  private void addSuggestion(Builder builder, String term) {
    Map<String, FieldSuggester> map = new HashMap<>();
    map.put(Suggest.DID_YOU_MEAN, FieldSuggester.of(fs -> fs.phrase(p ->
            p.maxErrors(2.0).size(5).field(FieldAttr.Movie.TITLE_SUGGEST)
        )
    ));
    Suggester suggester = Suggester.of(sg -> sg
        .suggesters(map)
        .text(term)
    );
    builder.suggest(suggester);
  }

  public SearchResponse<MovieSuggestDTO> autocomplete(String term, int size) throws IOException {
    Map<String, FieldSuggester> map = new HashMap<>();
    map.put(Suggest.TITLE_SUGGEST_NAME, FieldSuggester.of(fs -> fs
        .completion(cs -> cs.skipDuplicates(true)
            .size(size)
            .fuzzy(SuggestFuzziness.of(sf -> sf.fuzziness("1").transpositions(true).minLength(2).prefixLength(3)))
            .field(Suggest.TITLE_SUGGEST)
        )
    ));
    Suggester suggester = Suggester.of(s -> s
        .suggesters(map)
        .text(term)
    );
    SearchRequest searchRequest = SearchRequest.of(s -> {
      s.index(index)
          .source(SourceConfig.of(sc -> sc.filter(f -> f.includes(List.of(FieldAttr.Movie.TITLE_FIELD)))))
          .suggest(suggester);
      return s;
    });
    return client.search(searchRequest, MovieSuggestDTO.class);
  }

  private void addQuery(Builder builder, String term) {
    if (isEmpty(term)) {
      builder.query(Query.of(q -> q.matchAll(MatchAllQuery.of(ma -> ma))));
    } else {
      var matchQuery = Query.of(q -> q.match(MatchQuery.of(m -> m.field("title").query(term).operator(Operator.And).boost(10f))));
      var multiMatchQuery = Query.of(q -> q.multiMatch(MultiMatchQuery.of(m ->
          m.fields("title", "description", "actors", "director").operator(Operator.Or).query(term))));
      var boolQuery = BoolQuery.of(
          bq -> {
            bq.should(matchQuery, multiMatchQuery);
            return bq;
          }
      );
      builder.query(Query.of(q -> q.bool(boolQuery)));
    }
  }

  private void addSearchAfter(List<String> searchAfter, SearchRequest.Builder s) {
    if (!searchAfter.isEmpty()) {
      s.searchAfter(searchAfter);
    }
  }

  private void addSort(SearchRequest.Builder s) {
    s.sort(SortOptions.of(so -> so
        .field(FieldSort.of(fs -> fs.field("_score").order(SortOrder.Desc)))));
    s.sort(SortOptions.of(so -> so
        .field(FieldSort.of(fs -> fs.field("code").order(SortOrder.Asc)))));
  }
}
