package com.sicredi.votingsession.client.response;

import com.sicredi.votingsession.enums.UserVotingStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserClientResponse {
  
  UserVotingStatus status;
}
