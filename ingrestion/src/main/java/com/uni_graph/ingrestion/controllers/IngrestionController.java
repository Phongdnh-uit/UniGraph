package com.uni_graph.ingrestion.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Ingrestion Controller",
    description = "Controller for handling ingrestion related operations")
@RequestMapping("/api/v1/ingrestion")
@RestController
public class IngrestionController {

  @PostMapping("/ingest-by-csv")
  public ResponseEntity<String> ingestData() {
    return ResponseEntity.ok("Data ingested successfully!");
  }

  @PostMapping("/ingrest-by-crawler")
  public ResponseEntity<String> ingestByCrawler() {
    return ResponseEntity.ok("Data ingested successfully by crawler!");
  }
}
