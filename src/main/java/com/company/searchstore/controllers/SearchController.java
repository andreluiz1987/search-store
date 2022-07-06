package com.company.searchstore.controllers;

import com.company.searchstore.dto.MovieCatalogDTO;
import com.company.searchstore.dto.SearchDTO;
import com.company.searchstore.services.SearchService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

  @GetMapping("all")
  public ResponseEntity<MovieCatalogDTO> getAll(@Valid SearchDTO searchDTO) throws IOException {
    return ResponseEntity.ok(service.getAll(searchDTO));
  }

  @GetMapping("search")
  public ResponseEntity<MovieCatalogDTO> search() {
    return null;
  }

  @GetMapping("autocomplete")
  public ResponseEntity<List<String>> getSuggestions() {
    return ResponseEntity.ok(service.getSuggestions());
  }

  @GetMapping("facets")
  public ResponseEntity<Map<String, Integer>> getFacets() {
    return ResponseEntity.ok(service.getFacets());
  }
}
