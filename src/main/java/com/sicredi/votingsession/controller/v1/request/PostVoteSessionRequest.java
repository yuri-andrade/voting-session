package com.sicredi.votingsession.controller.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostVoteSessionRequest {
  
  @Schema(description = "Topic id", example = "60cf1e9b480542724d07ff8d")
  @NotBlank
  String topicId;
  @Schema(description = "Duration of the session in minutes", implementation = Long.class, example = "10", defaultValue = "1")
  @Positive
  Long duration;
}
