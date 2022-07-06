package com.company.searchstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {

  private int code;
  private int year;
  private String director;
  private double rating;
  private String description;
  private int runtime;
  private String title;
  List<String> actors;
  private double revenue;
  List<String> genre;
  private int votes;
  private int metascore;
  @JsonProperty("search_after")
  private Object[] searchAfter;
}
