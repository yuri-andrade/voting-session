package com.sicredi.votingsession.controller.v1.request;

import com.sicredi.votingsession.enums.UserVoteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostVoteRequest {
  
  @NotBlank
  @Schema(description = "Session id", example = "60cf1e9b480542724d07ff8d")
  String voteSessionId;
  @NotBlank
  @Schema(description = "User cpf", example = "31216747067")
  String cpf;
  @NotNull
  @Schema(description = "Vote in favor/against the topic", implementation = UserVoteEnum.class)
  UserVoteEnum vote;
}
