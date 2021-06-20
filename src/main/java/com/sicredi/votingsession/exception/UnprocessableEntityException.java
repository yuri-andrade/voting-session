package com.sicredi.votingsession.exception;

import com.sicredi.votingsession.configuration.MessageError.ApiError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UnprocessableEntityException extends RuntimeException {
  
  private final ApiError error;
  
  public UnprocessableEntityException(ApiError error) {
    super(error.toString());
    this.error = error;
  }
}
