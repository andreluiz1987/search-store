package com.company.searchstore.controllers;

import com.company.searchstore.dto.FacetsDTO;
import com.company.searchstore.dto.MovieCatalogDTO;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.services.SearchHLRCService;
import com.company.searchstore.services.SearchService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = {"api/movies"})
@Slf4j
@AllArgsConstructor
public class SearchController {

  private final SearchService service;
  private final SearchHLRCService searchHLRC;

  @GetMapping("all")
  public ResponseEntity<MovieCatalogDTO> getAll(@Valid SearchDTO searchDTO) throws IOException {
    return ResponseEntity.ok(service.search(searchDTO));
  }

  @GetMapping("search")
  public ResponseEntity<MovieCatalogDTO> search(@Valid SearchDTO searchDTO) throws IOException {
    return ResponseEntity.ok(searchHLRC.search(searchDTO));
  }

  @GetMapping("mlt")
  public ResponseEntity<MovieCatalogDTO> recommedation(String code) throws IOException {
    return ResponseEntity.ok(service.getMoreLikeThis(code));
  }

  @GetMapping("autocomplete")
  public ResponseEntity<Set<String>> getSuggestions(@Valid SearchDTO searchDTO) throws IOException {
    return ResponseEntity.ok(service.getSuggestions(searchDTO));
  }

  @GetMapping("facets")
  public ResponseEntity<Map<String, List<FacetsDTO>>> getFacets(@Valid SearchDTO searchDTO) throws IOException {
    return ResponseEntity.ok(service.getFacets(searchDTO));
  }
}
