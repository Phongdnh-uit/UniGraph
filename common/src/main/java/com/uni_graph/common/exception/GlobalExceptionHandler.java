package com.uni_graph.common.exception;

import com.uni_graph.common.dto.ApiResponse;
import com.uni_graph.common.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();

    String message =
        messageSource.getMessage(
            errorCode.getMessageKey(),
            null,
            errorCode.getMessageKey(),
            LocaleContextHolder.getLocale());

    ApiResponse.ErrorDetails errorDetails =
        ApiResponse.ErrorDetails.builder()
            .code(errorCode.getCode())
            .details(exception.getDetails())
            .build();

    ApiResponse<Void> apiResponse =
        ApiResponse.<Void>builder()
            .status(errorCode.getHttpStatus().value())
            .message(message)
            .error(errorDetails)
            .build();

    return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception exception) {
    log.error("Uncategorized exception occurred: ", exception);

    ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

    String message =
        messageSource.getMessage(
            errorCode.getMessageKey(),
            null,
            "Internal Server Error",
            LocaleContextHolder.getLocale());

    ApiResponse.ErrorDetails errorDetails =
        ApiResponse.ErrorDetails.builder().code(errorCode.getCode()).build();

    ApiResponse<Void> apiResponse =
        ApiResponse.<Void>builder()
            .status(errorCode.getHttpStatus().value())
            .message(message)
            .error(errorDetails)
            .build();

    return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
  }
}
