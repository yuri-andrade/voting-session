package com.sicredi.votingsession.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
@Schema(description = "Generic response with the created resource id")
public class Response {
  @Schema(description = "Created resource id", example = "60cf1e04480542724d07ff8c")
  String id;
}
