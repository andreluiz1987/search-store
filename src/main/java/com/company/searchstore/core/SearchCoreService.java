package com.company.searchstore.core;

import static org.apache.http.util.TextUtils.isEmpty;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
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
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest.Builder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.PhraseSuggester;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.company.searchstore.core.fields.FieldAttr;
import com.company.searchstore.core.fields.FieldAttr.Aggregations;
import com.company.searchstore.core.fields.FieldAttr.Suggest;
import com.company.searchstore.dto.MovieSuggestDTO;
import com.company.searchstore.models.Movie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCoreService {

  @Value("${es.indexName}")
  private String index;
  private final ElasticsearchClient client;

  public SearchResponse<Movie> searchTerm(String term, int size, List<String> searchAfter, Map<String, List<String>> filters) throws IOException {
    SearchRequest searchRequest = getSearchRequest(term, size, searchAfter, Collections.emptyMap(), filters);
    return client.search(searchRequest, Movie.class);
  }

  public SearchResponse<Void> getFacets(String term, List<String> searchAfter, Map<String, List<String>> filters) throws IOException {
    Map<String, Aggregation> map = new HashMap<>();
    map.put(Aggregations.FACET_GENRE_NAME, new Aggregation.Builder()
        .terms(new TermsAggregation.Builder().field(Aggregations.FACET_GENRE).size(25).build())
        .build());
    map.put(Aggregations.FACET_CERTIFICATE_NAME, new Aggregation.Builder()
        .terms(new TermsAggregation.Builder().field(Aggregations.FACET_CERTIFICATE).size(10).build())
        .build());
    SearchRequest searchRequest = getSearchRequest(term, 0, searchAfter, map, filters);
    return client.search(searchRequest, Void.class);
  }

  private SearchRequest getSearchRequest(String term,
      int size,
      List<String> searchAfter,
      Map<String, Aggregation> map, Map<String, List<String>> filters) {
    return SearchRequest.of(s -> {
      s.index(index);
      s.size(size);
      addQuery(s, term, filters);
      addSearchAfter(searchAfter, s);
      addSort(s);
      addAggregation(map, s);
      return s;
    });
  }

  private void addAggregation(Map<String, Aggregation> map, Builder s) {
    if (!map.isEmpty()) {
      s.aggregations(map);
    }
  }

  private void addSuggestion(Builder builder, String term) {
    Map<String, FieldSuggester> map = new HashMap<>();
    map.put(Suggest.DID_YOU_MEAN, FieldSuggester.of(fs -> fs.phrase(p ->
            p.maxErrors(2.0).size(5).field(FieldAttr.Movie.TITLE_SUGGEST)
        )
    ));
    Suggester suggester = Suggester.of(sg -> sg
        .suggesters("did_you_mean", new FieldSuggester.Builder()
            .phrase(PhraseSuggester.of(p ->
                p.maxErrors(2.0).size(5).field(FieldAttr.Movie.TITLE_SUGGEST)))
            .build())
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

  private void addQuery(Builder builder, String term, Map<String, List<String>> filters) {
    if (isEmpty(term)) {
      builder.query(Query.of(q -> q.matchAll(MatchAllQuery.of(ma -> ma))));
    } else {
      buildBoolQuery(builder, term, filters);
    }
  }

  private void buildBoolQuery(Builder builder, String term, Map<String, List<String>> mapFilters) {
    var filters = getFilters(mapFilters);
    var matchQuery = Query
        .of(q -> q.match(MatchQuery.of(m -> m.field(FieldAttr.Movie.TITLE_FIELD).query(term).operator(Operator.And).boost(10f))));
    var multiMatchQuery = Query.of(q -> q.multiMatch(MultiMatchQuery.of(m ->
        m.fields(
            applyFieldBoost(FieldAttr.Movie.TITLE_FIELD, 5),
            applyFieldBoost(FieldAttr.Movie.DESCRIPTION_FIELD, 2),
            applyFieldBoost(FieldAttr.Movie.ACTORS_SUGGEST, 5))
            .operator(Operator.Or).query(term))));
    var boolQuery = BoolQuery.of(
        bq -> {
          bq.filter(filters);
          bq.should(matchQuery, multiMatchQuery);
          bq.minimumShouldMatch("1");
          return bq;
        }
    );
    builder.query(Query.of(q -> q.bool(boolQuery)));
  }

  private List<Query> getFilters(Map<String, List<String>> mapFilters) {
    var queries = new ArrayList<Query>();
    var genres = mapFilters.get("genres");
    if (!genres.isEmpty()) {
      var filters = genres.stream().map(g -> FieldValue.of(fv -> fv.stringValue(g))).collect(Collectors.toList());
      queries.add(getTermsQuery(filters, FieldAttr.Movie.GENRE_FIELD));
    }

    var certificates = mapFilters.get("certificates");
    if (!certificates.isEmpty()) {
      var filters = certificates.stream().map(g -> FieldValue.of(fv -> fv.stringValue(g))).collect(Collectors.toList());
      queries.add(getTermsQuery(filters, FieldAttr.Movie.CERTIFICATE_FIELD));
    }
    return queries;
  }

  private Query getTermsQuery(List<FieldValue> filters, String field) {
    var termsGenre = Query.of(q ->
        q.terms(TermsQuery.of(tsq ->
            tsq.field(field)
                .terms(TermsQueryField.of(tf -> tf.value(
                    filters
                ))))));
    return termsGenre;
  }

  private String applyFieldBoost(String field, int boost) {
    return String.format("%s^%s", field, boost);
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
