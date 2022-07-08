package com.company.searchstore.services;

import static com.company.searchstore.core.fields.FieldAttr.Suggest.DID_YOU_MEAN;

import com.company.searchstore.core.SearchCoreHLRCService;
import com.company.searchstore.dto.MovieCatalogDTO;
import com.company.searchstore.dto.MovieDTO;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.mappers.MovieMapper;
import com.company.searchstore.models.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHLRCService {

  private final MovieMapper mapper;
  private final SearchCoreHLRCService serviceHLRC;
  private final ObjectMapper objectMapper;

  public MovieCatalogDTO search(SearchDTO searchDTO) throws IOException {
    var response = serviceHLRC.searchTerm(searchDTO.getText(), searchDTO.getSize(), searchDTO.getSearchAfter(), searchDTO.getMapFilters());
    var movies = new ArrayList<MovieDTO>();
    getResultDocuments(response, movies);
    return MovieCatalogDTO.builder()
        .movies(movies)
        .size(searchDTO.getSize())
        .total(getTotalHits(response))
        .suggestion(getSuggestion(response.getSuggest()))
        .build();
  }

  private String getSuggestion(Suggest suggest) {
    if (Objects.nonNull(suggest)) {
      var suggestValue = suggest.getSuggestion(DID_YOU_MEAN);
      if (Objects.nonNull(suggestValue)) {
        var options = suggestValue.getEntries().stream().map(Entry::getOptions).collect(Collectors.toList());

        var items = (List<Option>) options.stream().flatMap(List::stream).collect(Collectors.toList());
        if (items.isEmpty()) {
          return "";
        }
        return items.get(0).getText().toString();
      }
      return ((Option) suggestValue.getEntries().get(0).getOptions().get(0)).getText().toString();
    }

    return "";
  }

  private void getResultDocuments(SearchResponse response, ArrayList<MovieDTO> movies) throws JsonProcessingException {
    for (var hit : response.getHits()) {
      var movie = objectMapper.readValue(hit.getSourceAsString(), Movie.class);
      var dto = mapper.toDto(movie);
      dto.setSearchAfter(new Object[]{hit.getScore(), dto.getCode()});
      movies.add(dto);
    }
  }

  private long getTotalHits(SearchResponse response) {
    return Objects.nonNull(response.getHits().getTotalHits()) ? response.getHits().getTotalHits().value : 0;
  }

}
