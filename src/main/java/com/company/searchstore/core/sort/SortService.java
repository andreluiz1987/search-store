package com.company.searchstore.core.sort;

import org.elasticsearch.search.builder.SearchSourceBuilder;

public interface SortService {

  void addSort(SearchSourceBuilder builder);
}
