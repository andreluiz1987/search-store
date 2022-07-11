package com.company.searchstore.dto;

import com.company.searchstore.core.sort.SortEnum;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.springframework.util.CollectionUtils;

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

  @Singular
  private List<String> genres;

  @Singular
  private List<String> certificates;

  private SortEnum sort = SortEnum.MOST_POPULAR;

  public List<String> getSearchAfter() {
    return Objects.nonNull(searchAfterCode) && Objects.nonNull(searchAfterScore) ? List.of(searchAfterScore.toString(), searchAfterCode.toString()) :
        Collections.emptyList();
  }

  public Map<String, List<String>> getMapFilters() {
    return new HashMap<>() {{
      put("genres", CollectionUtils.isEmpty(genres) ? Collections.emptyList() : genres);
      put("certificates", CollectionUtils.isEmpty(certificates) ? Collections.emptyList() : certificates);
    }};
  }
}
