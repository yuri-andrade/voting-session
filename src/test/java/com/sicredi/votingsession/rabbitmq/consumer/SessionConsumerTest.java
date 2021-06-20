package com.sicredi.votingsession.rabbitmq.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import com.sicredi.votingsession.controller.v1.response.SessionResultResponse;
import com.sicredi.votingsession.domain.Session;
import com.sicredi.votingsession.rabbitmq.producer.SessionProducer;
import com.sicredi.votingsession.service.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SessionConsumerTest {
  
  @Mock
  private SessionService sessionService;
  @Mock
  private SessionProducer sessionProducer;
  
  @InjectMocks
  private SessionConsumer sessionConsumer;
  
  @Test
  void givenExpiredSession_whenProcessExpiredSession_thenProduceResult() {
    var session = Session.builder().build();
    var sessionResult = SessionResultResponse.builder().build();
    Mockito.when(sessionService.getVotingSessionResult(any())).thenReturn(Mono.just(sessionResult));
    
    StepVerifier.create(sessionConsumer.processExpiredSession(session)).verifyComplete();
    verify(sessionProducer).postSessionResult(any());
  }
}
