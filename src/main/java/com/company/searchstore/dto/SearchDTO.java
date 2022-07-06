package com.company.searchstore.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {

  @NotNull
  private String text;

  @Min(10)
  @Max(20)
  private int size = 15;

  private Object searchAfterScore;
  private Long searchAfterCode;

  public List<String> getSearchAfter() {
    return Objects.nonNull(searchAfterCode) && Objects.nonNull(searchAfterScore) ? List.of(searchAfterScore.toString(), searchAfterCode.toString()) :
        Collections.emptyList();
  }
}
