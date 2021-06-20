package com.sicredi.votingsession.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageError {
  
  private final MessageSource messageSource;
  
  public ApiError create(String code, String... replacements) {
    return new ApiError(code, messageSource.getMessage(code, replacements,
        LocaleContextHolder.getLocale()));
  }
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ApiError implements Serializable {
  
    @Schema(description = "Specific code for that error", example = "user.duplicated.vote")
    private String code;
    @Schema(description = "Message about the error", example = "The user already voted in this vote session")
    private String description;
    
  }
}
