package com.company.searchstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private String director;
  @JsonIgnore
  private String description;
  @JsonIgnore
  List<String> actors;
  @JsonIgnore
  private int code;
  @JsonIgnore
  private int year;
  @JsonIgnore
  private String avatar;
  private String certificate;
  @JsonIgnore
  private double rating;
  @JsonIgnore
  private String runtime;
  @JsonIgnore
  private String revenue;
  List<String> genre;
  @JsonIgnore
  private int votes;
  @JsonIgnore
  private int metascore;
  @JsonProperty("search_after")
  private Object[] searchAfter;
}
