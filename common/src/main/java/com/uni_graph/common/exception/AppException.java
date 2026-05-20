package com.uni_graph.common.exception;

import com.uni_graph.common.enums.ErrorCode;
import java.io.Serial;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  private final ErrorCode errorCode;
  private final Object details;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessageKey());
    this.errorCode = errorCode;
    this.details = null;
  }

  public AppException(ErrorCode errorCode, Object details) {
    super(errorCode.getMessageKey());
    this.errorCode = errorCode;
    this.details = details;
  }
}
