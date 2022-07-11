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

  private String title;
  private String director;

  private String description;

  List<String> actors;

  private int code;

  private int year;

  private String avatar;
  private String certificate;

  private double rating;

  private String runtime;

  private String revenue;
  List<String> genre;

  private int votes;

  private int metascore;
  @JsonProperty("search_after")
  private Object[] searchAfter;
}
