package com.company.searchstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

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
}
