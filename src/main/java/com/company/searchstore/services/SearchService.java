package com.company.searchstore.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.company.searchstore.core.SearchCoreService;
import com.company.searchstore.dto.MovieCatalogDTO;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.mappers.MovieMapper;
import com.company.searchstore.models.Movie;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final MovieMapper mapper;
  private final SearchCoreService service;

  public MovieCatalogDTO getAll(SearchDTO searchDTO) throws IOException {
    var response = service.getAll(searchDTO.getSize(), searchDTO.getFrom());
    var movies = response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
    return MovieCatalogDTO.builder()
        .movies(mapper.toDtos(movies))
        .size(getTotalHits(response))
        .build();
  }

  public MovieCatalogDTO search(SearchDTO searchDTO) throws IOException {
    return MovieCatalogDTO.builder()
        .build();
  }

  public List<String> getSuggestions() {
    return List.of("");
  }

  public Map<String, Integer> getFacets() {
    return Map.of("", 0);
  }

  private long getTotalHits(SearchResponse<Movie> response) {
    return Objects.nonNull(response.hits().total()) ? response.hits().total().value() : 0;
  }
}
