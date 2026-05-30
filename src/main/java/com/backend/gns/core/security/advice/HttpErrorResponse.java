package com.backend.gns.core.security.advice;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

@Setter
@Getter
@RequiredArgsConstructor
@Data
public class HttpErrorResponse {

  private HttpStatus status;

  private int statusCode;

  private String message;

  private String reason;

  private List<FieldError> validations;
}
