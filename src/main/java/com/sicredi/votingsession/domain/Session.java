package com.sicredi.votingsession.domain;

import com.sicredi.votingsession.controller.v1.request.PostVoteSessionRequest;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class Session {
  
  public static final int DEFAULT_DURATION = 1;
  @Id
  private String id;
  private String topic;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  
  
  public static Session create(PostVoteSessionRequest postVoteSessionRequest, Topic topic) {
    var startDate = LocalDateTime.now();
    if (postVoteSessionRequest.getDuration() == null) {
      return defaultTimeSession(startDate, topic);
    } else {
      return customTimeSession(postVoteSessionRequest, startDate, topic);
    }
  }
  
  private static Session defaultTimeSession(LocalDateTime startDate, Topic topic) {
    return Session.builder()
        .topic(topic.getName())
        .startDate(startDate)
        .endDate(startDate.plusMinutes(DEFAULT_DURATION))
        .build();
  }
  
  private static Session customTimeSession(PostVoteSessionRequest postVoteSessionRequest,
      LocalDateTime startDate, Topic topic) {
    return Session.builder()
        .topic(topic.getName())
        .startDate(startDate)
        .endDate(startDate.plusMinutes(postVoteSessionRequest.getDuration()))
        .build();
  }
}

