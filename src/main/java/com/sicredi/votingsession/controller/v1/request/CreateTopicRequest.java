package com.sicredi.votingsession.controller.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateTopicRequest {
  
  @Schema(description = "Topic name", example = "Test topic")
  @NotBlank
  String name;
}
