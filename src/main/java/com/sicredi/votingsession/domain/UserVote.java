package com.sicredi.votingsession.domain;

import com.sicredi.votingsession.enums.UserVoteEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@CompoundIndex(def = "{'cpf' : 1, 'sessionId': 1}", unique = true)
@RequiredArgsConstructor
public class UserVote {
  
  @Id
  private String id;
  private final String cpf;
  private final String sessionId;
  private final LocalDateTime voteDate;
  private final UserVoteEnum vote;
}
