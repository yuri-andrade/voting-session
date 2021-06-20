package com.sicredi.votingsession.exception;

import com.sicredi.votingsession.configuration.MessageError.ApiError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class NotFoundException extends RuntimeException {
  
  private final ApiError error;
  
  public NotFoundException(ApiError error) {
    super(error.toString());
    this.error = error;
  }
}
