package com.uni_graph.ingestion.controllers;

import com.uni_graph.ingestion.dto.ApiResponse;
import com.uni_graph.ingestion.enums.ErrorCode;
import com.uni_graph.ingestion.exception.AppException;
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
    name = "Ingestion Controller",
    description = "Controller for handling ingestion related operations")
@RequestMapping("/api/v1/ingestion")
@RestController
@RequiredArgsConstructor
public class IngestionController {

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
  public ResponseEntity<ApiResponse<String>> ingestData(
      @Parameter(
              description = "CSV file to upload",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestParam("file")
          MultipartFile file)
      throws IOException {

    if (file.isEmpty()) {
      throw new AppException(ErrorCode.CSV_VALIDATION_FAILED, "Please upload a CSV file.");
    }

    csvIngestionService.ingestCoursesFromCsv(file.getInputStream());

    return ResponseEntity.ok(
        ApiResponse.<String>builder().message("Data ingested successfully!").build());
  }

  @PostMapping("/ingest-by-crawler")
  public ResponseEntity<ApiResponse<String>> ingestByCrawler() {
    return ResponseEntity.ok(
        ApiResponse.<String>builder().message("Data ingested successfully by crawler!").build());
  }

  @Operation(
      summary = "Ingest course summaries from a CSV file",
      description =
          "Upload a CSV file containing course summaries. Required columns: \n"
              + "1. course_code (String)\n"
              + "2. course_name_vi (String)\n"
              + "3. course_summary (String)")
  @PostMapping(value = "/summaries", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<String>> ingestCourseSummaries(
      @Parameter(
              description = "CSV file to upload",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestParam("file")
          MultipartFile file)
      throws IOException {

    if (file.isEmpty()) {
      throw new AppException(ErrorCode.CSV_VALIDATION_FAILED, "Please upload a CSV file.");
    }

    csvIngestionService.ingestCourseSummariesFromCsv(file.getInputStream());

    return ResponseEntity.ok(
        ApiResponse.<String>builder()
            .message("Course summaries ingested and re-embedding triggered successfully!")
            .build());
  }
}
