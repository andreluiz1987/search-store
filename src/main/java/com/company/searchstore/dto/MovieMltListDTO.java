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
public class MovieMltListDTO {

  private long size;
  private List<MovieMltDTO> movies;
}
