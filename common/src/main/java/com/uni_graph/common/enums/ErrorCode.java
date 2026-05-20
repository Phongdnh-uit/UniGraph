package com.uni_graph.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "error.uncategorized", "COMMON_001"),
  INVALID_KEY(HttpStatus.BAD_REQUEST, "error.invalid_key", "COMMON_002"),

  // Ingestion related errors
  INGESTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "error.ingestion_failed", "INGEST_001"),
  FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.file_read_error", "INGEST_002"),
  COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.course_not_found", "INGEST_003"),
  CSV_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "error.csv_validation_failed", "INGEST_004"),
  ;

  private final HttpStatus httpStatus;
  private final String messageKey;
  private final String code;

  ErrorCode(HttpStatus httpStatus, String messageKey, String code) {
    this.httpStatus = httpStatus;
    this.messageKey = messageKey;
    this.code = code;
  }
}
