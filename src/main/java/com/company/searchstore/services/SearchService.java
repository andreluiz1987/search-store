package com.company.searchstore.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.company.searchstore.core.SearchCoreService;
import com.company.searchstore.core.fields.FieldAttr.Suggest;
import com.company.searchstore.dto.MovieCatalogDTO;
import com.company.searchstore.dto.MovieDTO;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.mappers.MovieMapper;
import com.company.searchstore.models.Movie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final MovieMapper mapper;
  private final SearchCoreService service;

  public MovieCatalogDTO search(SearchDTO searchDTO) throws IOException {
    var response = service.searchTerm(searchDTO.getText(), searchDTO.getSize(), searchDTO.getSearchAfter());
    var movies = new ArrayList<MovieDTO>();
    getResultDocuments(response, movies);
    return MovieCatalogDTO.builder()
        .movies(movies)
        .size(searchDTO.getSize())
        .total(getTotalHits(response))
        .suggestion("")
        .build();
  }

  private void getResultDocuments(SearchResponse<Movie> response, ArrayList<MovieDTO> movies) {
    for (var hit : response.hits().hits()) {
      var dto = mapper.toDto(hit.source());
      dto.setSearchAfter(new Object[]{hit.score(), dto.getCode()});
      movies.add(dto);
    }
  }

  public Set<String> getSuggestions(SearchDTO searchDTO) throws IOException {
    var suggestionMovies = new HashSet<String>();
    var response = service.autocomplete(searchDTO.getText(), searchDTO.getSize());
    var suggestions = response.suggest().get(Suggest.TITLE_SUGGEST_NAME);
    for (var item : suggestions) {
      suggestionMovies.addAll(item.completion().options().stream().map(o -> o.source().getTitle()).collect(Collectors.toSet()));
    }
    return suggestionMovies;
  }

  public Map<String, Integer> getFacets() {
    return Map.of("", 0);
  }

  private long getTotalHits(SearchResponse<Movie> response) {
    return Objects.nonNull(response.hits().total()) ? response.hits().total().value() : 0;
  }
}
