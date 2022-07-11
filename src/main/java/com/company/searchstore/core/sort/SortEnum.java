package com.company.searchstore.core.sort;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SortEnum {

  @JsonProperty("most_popular")
  MOST_POPULAR("most_popular-popular"),

  @JsonProperty("best_rating")
  BEST_RATING("best_rating");

  private String value;

  SortEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}