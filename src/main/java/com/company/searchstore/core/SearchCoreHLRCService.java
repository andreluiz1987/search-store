package com.company.searchstore.core;

import static com.company.searchstore.core.fields.FieldAttr.Suggest.DID_YOU_MEAN;

import com.company.searchstore.core.fields.FieldAttr;
import com.company.searchstore.core.fields.FieldAttr.Movie;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SearchCoreHLRCService {

  @Value("${es.indexName}")
  private String index;
  private final RestHighLevelClient client;

  public SearchResponse searchTerm(String term, int size, List<String> searchAfter, Map<String, List<String>> filters)
      throws IOException {

    var searchRequest = new SearchRequest();
    var searchSourceBuilder = new SearchSourceBuilder();
    var boolQuery = new BoolQueryBuilder();

    searchSourceBuilder.size(size);

    addQuery(term, searchSourceBuilder, boolQuery);

    addFilters(filters, boolQuery);

    addSearchAfter(searchAfter, searchSourceBuilder);

    addSort(searchSourceBuilder);

    addSuggestion(term, searchSourceBuilder);

    searchRequest.indices(index);
    searchRequest.source(searchSourceBuilder);

    return client.search(searchRequest, RequestOptions.DEFAULT);
  }

  private void addSuggestion(String term, SearchSourceBuilder searchSourceBuilder) {
    var suggestBuilder = new SuggestBuilder();
    suggestBuilder.addSuggestion(DID_YOU_MEAN, SuggestBuilders.phraseSuggestion(FieldAttr.Suggest.TITLE_SUGGEST)
        .size(3)
        .maxErrors(4)
        .text(term));
    searchSourceBuilder.suggest(suggestBuilder);
  }

  private void addQuery(String term, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQuery) {
    if (StringUtils.hasText(term)) {
      boolQuery
          .minimumShouldMatch(1)
          .should(QueryBuilders.multiMatchQuery(term)
              .field(Movie.TITLE_FIELD, 5)
              .field(Movie.ACTORS_SUGGEST, 5)
              .field(Movie.DESCRIPTION_FIELD, 2))
          .should(QueryBuilders.matchQuery(Movie.TITLE_FIELD, term).operator(Operator.AND).boost(10));
      searchSourceBuilder.query(boolQuery);
    } else {
      searchSourceBuilder.query(QueryBuilders.matchAllQuery());
    }
  }

  private void addFilters(Map<String, List<String>> filters, BoolQueryBuilder boolQuery) {
    var genres = filters.get("genres");
    if (!genres.isEmpty()) {
      boolQuery.filter(QueryBuilders.termsQuery(Movie.GENRE_FIELD, genres));
    }

    var certificates = filters.get("certificates");
    if (!certificates.isEmpty()) {
      boolQuery.filter(QueryBuilders.termsQuery(Movie.CERTIFICATE_FIELD, certificates));
    }
  }

  private void addSearchAfter(List<String> searchAfter, SearchSourceBuilder searchSourceBuilder) {
    if (!searchAfter.isEmpty()) {
      searchSourceBuilder.searchAfter(searchAfter.toArray());
    }
  }

  private void addSort(SearchSourceBuilder searchSourceBuilder) {
    searchSourceBuilder
        .sort(SortBuilders.fieldSort("_score").order(SortOrder.DESC))
        .sort(SortBuilders.fieldSort("code").order(SortOrder.ASC));
  }
}
