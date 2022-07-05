package com.company.searchstore.controllers;

import com.company.searchstore.services.SearchService;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = {"api/v1/movie"})
@Slf4j
@AllArgsConstructor
public class SearchController {

  private final SearchService service;

  @GetMapping("search/")
  public ResponseEntity search() throws IOException {
    service.search();
    return ResponseEntity.ok().body("ok");
  }
}
