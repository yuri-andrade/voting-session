package com.sicredi.votingsession.rabbitmq.consumer;

import com.sicredi.votingsession.domain.Session;
import com.sicredi.votingsession.rabbitmq.producer.SessionProducer;
import com.sicredi.votingsession.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionConsumer {
  
  private final SessionService sessionService;
  private final SessionProducer sessionProducer;
  
  @RabbitListener(queues = "vote-session-dlq")
  public Mono<Void> processExpiredSession(Session session) {
    return sessionService.getVotingSessionResult(session.getId())
        .flatMap(sessionResultResponse -> {
          sessionProducer.postSessionResult(sessionResultResponse);
          return Mono.empty();
        });
  }
}
