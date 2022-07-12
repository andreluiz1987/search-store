package com.company.searchstore.services;

import static com.company.searchstore.core.fields.FieldAttr.Aggregations.FACET_CERTIFICATE_NAME;
import static com.company.searchstore.core.fields.FieldAttr.Aggregations.FACET_GENRE_NAME;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.company.searchstore.core.SearchCoreService;
import com.company.searchstore.core.fields.FieldAttr.Suggest;
import com.company.searchstore.dto.FacetsDTO;
import com.company.searchstore.dto.MovieCatalogDTO;
import com.company.searchstore.dto.MovieDTO;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.mappers.MovieMapper;
import com.company.searchstore.models.Movie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    var response = service.searchTerm(searchDTO.getText(), searchDTO.getSize(), searchDTO.getSearchAfter(), searchDTO.getMapFilters());
    var movies = new ArrayList<MovieDTO>();
    getResultDocuments(response, movies);
    return MovieCatalogDTO.builder()
        .movies(movies)
        .size(searchDTO.getSize())
        .sort(searchDTO.getSort())
        .total(getTotalHits(response))
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

  public Map<String, List<FacetsDTO>> getFacets(SearchDTO searchDTO) throws IOException {
    var response = service.getFacets(searchDTO.getText(), searchDTO.getSearchAfter(), searchDTO.getMapFilters());
    return parseResults(response, List.of(FACET_GENRE_NAME, FACET_CERTIFICATE_NAME));
  }

  private Map<String, List<FacetsDTO>> parseResults(SearchResponse<Void> response, List<String> aggNames) {
    Map<String, List<FacetsDTO>> facets = new HashMap<>();
    for (var name : aggNames) {
      var list = response.aggregations().get(name).sterms().buckets().array();
      var facetsList = list.stream().map(l -> new FacetsDTO(l.key(), l.docCount())).collect(Collectors.toList());
      facets.put(name, facetsList);
    }
    return facets;
  }

  private long getTotalHits(SearchResponse<Movie> response) {
    return Objects.nonNull(response.hits().total()) ? response.hits().total().value() : 0;
  }
}
