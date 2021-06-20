package com.sicredi.votingsession.rabbitmq.producer;

import com.sicredi.votingsession.controller.v1.response.SessionResultResponse;
import com.sicredi.votingsession.domain.Session;
import com.sicredi.votingsession.rabbitmq.configuration.CustomMessagePostProcessor;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionProducer {
  
  private final AmqpTemplate amqpTemplate;
  @Value("${vote-session-exchange}")
  private String voteSessionExchange;
  
  public void postVoteSessionStarted(Session session) {
    var messageTtl = ChronoUnit.MILLIS.between(session.getStartDate(), session.getEndDate());
    var customMessagePostProcessor = new CustomMessagePostProcessor(messageTtl);
    
    amqpTemplate.convertAndSend(voteSessionExchange, "session-started", session,
        customMessagePostProcessor);
  }
  
  public void postSessionResult(SessionResultResponse sessionResultResponse) {
    amqpTemplate.convertAndSend(voteSessionExchange, "session-result", sessionResultResponse);
  }
}
