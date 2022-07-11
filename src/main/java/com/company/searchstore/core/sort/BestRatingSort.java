package com.company.searchstore.core.sort;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class BestRatingSort implements SortService {

  @Override
  public void addSort(SearchSourceBuilder builder) {
    builder.sort(SortBuilders.fieldSort("rating").order(SortOrder.DESC));
    builder.sort(SortBuilders.fieldSort("code").order(SortOrder.ASC));
  }
}
