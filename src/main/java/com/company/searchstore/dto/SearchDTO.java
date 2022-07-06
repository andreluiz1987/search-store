package com.company.searchstore.dto;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {

  private String text;
  @Min(10)
  private int size;
  @Min(0)
  private int from;
}
