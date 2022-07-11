package com.company.searchstore.core.sort;

import java.util.EnumMap;

public final class SortFactory {

  private SortFactory() {
  }


  private static EnumMap<SortEnum, SortService> instances = new EnumMap<>(SortEnum.class);

  static {
    instances.put(SortEnum.BEST_RATING, new BestRatingSort());
    instances.put(SortEnum.MOST_POPULAR, new MostPopularSort());
  }

  public static SortService getInstance(SortEnum sortEnum) {
    return instances.get(sortEnum);
  }
}

