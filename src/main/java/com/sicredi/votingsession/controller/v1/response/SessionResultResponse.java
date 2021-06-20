package com.sicredi.votingsession.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionResultResponse {
  
  @Schema(description = "Session id", example = "60cf1e04480542724d07ff8c")
  private String id;
  @Schema(description = "Topic name", example = "test topic")
  private String topic;
  @Schema(description = "Number of votes in favor", example = "10", implementation = Long.class)
  private long voteYes;
  @Schema(description = "Number of votes against", example = "10", implementation = Long.class)
  private long voteNo;
}
