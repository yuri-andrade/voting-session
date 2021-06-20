package com.sicredi.votingsession.configuration;

import com.sicredi.votingsession.configuration.MessageError.ApiError;
import com.sicredi.votingsession.exception.NotFoundException;
import com.sicredi.votingsession.exception.UnprocessableEntityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandler {
  
  private final MessageError messageError;
  
  @org.springframework.web.bind.annotation.ExceptionHandler(value = NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<ApiError> handleUnprocessableEntityException(
      NotFoundException ex) {
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getError());
  }
  
  @org.springframework.web.bind.annotation.ExceptionHandler(value = UnprocessableEntityException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  protected ResponseEntity<ApiError> handleUnprocessableEntityException(
      UnprocessableEntityException ex) {
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getError());
  }
  
  @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<List<ApiError>> handleException(Exception ex) {
    ApiError error = messageError.create("unexpected.error");
    log.error(error.toString(), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        Collections.singletonList(error));
  }
  
  @org.springframework.web.bind.annotation.ExceptionHandler(value = WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public  ResponseEntity<List<ApiError>> handleException(WebExchangeBindException ex) {
    List<ApiError> errors = new ArrayList<>();
    ex.getFieldErrors()
        .forEach(fieldError -> {
          String rejectedValue = fieldError.getRejectedValue() == null ? "NULL"
              : fieldError.getRejectedValue().toString();
          errors.add(messageError.create("invalid.parameter", fieldError.getField(), rejectedValue,
              fieldError.getDefaultMessage()));
          log.error(ex.getMessage(), ex);
        });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.unmodifiableList(errors));
  }
  
  
}
