package com.company.searchstore.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieMltDTO {

  private String title;
  private String director;
  private String description;
  List<String> actors;
  private int code;
  private String avatar;
  private String certificate;
  private double rating;
  List<String> genre;
}
