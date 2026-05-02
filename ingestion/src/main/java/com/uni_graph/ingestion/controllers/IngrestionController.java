package com.uni_graph.ingestion.controllers;

import com.uni_graph.ingestion.service.CsvIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "Ingrestion Controller",
    description = "Controller for handling ingrestion related operations")
@RequestMapping("/api/v1/ingrestion")
@RestController
@RequiredArgsConstructor
public class IngrestionController {

  private final CsvIngestionService csvIngestionService;

  @Operation(
      summary = "Ingest courses from a CSV file",
      description =
          "Upload a CSV file containing course data. Required columns (in order): \n"
              + "1. id (Integer)\n"
              + "2. course_code (String, Unique)\n"
              + "3. course_name_vi (String)\n"
              + "4. course_name_en (String)\n"
              + "5. status (String)\n"
              + "6. department (String)\n"
              + "7. course_category (String)\n"
              + "8. old_course_code (String)\n"
              + "9. equivalent_course_codes (String, multi-line)\n"
              + "10. prerequisite_course_codes (String, multi-line)\n"
              + "11. previous_course_codes (String, multi-line)\n"
              + "12. theory_credits (Integer)\n"
              + "13. practical_credits (Integer)")
  @PostMapping(value = "/ingest-by-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> ingestData(
      @Parameter(
              description = "CSV file to upload",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestParam("file")
          MultipartFile file) {

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please upload a CSV file.");
    }

    try {
      csvIngestionService.ingestCoursesFromCsv(file.getInputStream());
      return ResponseEntity.ok("Data ingested successfully!");
    } catch (IOException e) {
      return ResponseEntity.internalServerError()
          .body("Error reading uploaded file: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Ingestion failed: " + e.getMessage());
    }
  }

  @PostMapping("/ingrest-by-crawler")
  public ResponseEntity<String> ingestByCrawler() {
    return ResponseEntity.ok("Data ingested successfully by crawler!");
  }
}
