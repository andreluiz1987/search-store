package com.company.searchstore.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieCatalogDTO {

  private long size;
  private long total;
  private List<MovieDTO> movies;
}