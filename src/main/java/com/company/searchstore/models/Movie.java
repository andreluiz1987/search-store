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

  private int code;
  private String avatar;
  private String certificate;
  private int year;
  private String director;
  private double rating;
  private String description;
  private String runtime;
  private String title;
  List<String> actors;
  private String revenue;
  List<String> genre;
  private int votes;
  private int metascore;
}
