package com.sicredi.votingsession.service;

import static com.sicredi.votingsession.domain.Session.DEFAULT_DURATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.configuration.MessageError.ApiError;
import com.sicredi.votingsession.controller.v1.request.PostVoteSessionRequest;
import com.sicredi.votingsession.domain.Session;
import com.sicredi.votingsession.domain.Topic;
import com.sicredi.votingsession.enums.UserVoteEnum;
import com.sicredi.votingsession.rabbitmq.producer.SessionProducer;
import com.sicredi.votingsession.repository.SessionRepository;
import com.sicredi.votingsession.repository.UserVoteRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
  
  @Mock
  private TopicService topicService;
  @Mock
  private SessionProducer sessionProducer;
  @Mock
  private SessionRepository sessionRepository;
  @Mock
  private UserVoteRepository userVoteRepository;
  @Mock
  private MessageError messageError;
  
  @InjectMocks
  private SessionService sessionService;
  
  @Nested
  class GetActiveVotingSessionTest {
    
    @Test
    void givenValidActiveVotingSessionId_whenGetActiveVotingSession_thenShouldNotThrow() {
      var session = Session.builder().build();
      when(sessionRepository.findByIdAndEndDateAfter(anyString(), any(LocalDateTime.class)))
          .thenReturn(Mono.just(session));
      
      StepVerifier.create(sessionService.getActiveVotingSession("123")).expectNext(session)
          .verifyComplete();
      
      Mockito.verifyNoInteractions(messageError);
    }
    
    @Test
    void givenNoActiveVotingSessionId_whenGetActiveVotingSession_thenShouldNotThrow() {
      when(sessionRepository.findByIdAndEndDateAfter(anyString(), any(LocalDateTime.class)))
          .thenReturn(Mono.empty());
      when(messageError.create(anyString())).thenReturn(new ApiError());
      
      StepVerifier.create(sessionService.getActiveVotingSession("123")).expectError().verify();
    }
  }
  
  @Nested
  class CreateVoteSessionTest {
    
    @Test
    void givenValidPostVoteSessionRequest_whenCreateVoteSession_thenShouldNotThrow() {
      var uuid = UUID.randomUUID().toString();
      var postSessionRequest = PostVoteSessionRequest.builder().topicId(uuid).build();
      var topic = new Topic("Test");
      var expectedSession = Session.create(postSessionRequest, topic);
      expectedSession.setId(UUID.randomUUID().toString());
      topic.setId(uuid);
      when(topicService.getTopicById(postSessionRequest.getTopicId())).thenReturn(Mono.just(topic));
      when(sessionRepository.save(any(Session.class))).thenReturn(Mono.just(expectedSession));
      
      StepVerifier.create(sessionService.createVoteSession(postSessionRequest))
          .expectNext(expectedSession.getId()).verifyComplete();
      verify(sessionProducer).postVoteSessionStarted(expectedSession);
    }
    
    @Test
    void givenError_whenCreateVoteSession_thenShouldNotProduceMessage() {
      var uuid = UUID.randomUUID().toString();
      var postSessionRequest = PostVoteSessionRequest.builder().topicId(uuid).build();
      var topic = new Topic("Test");
      var expectedSession = Session.create(postSessionRequest, topic);
      expectedSession.setId(UUID.randomUUID().toString());
      topic.setId(uuid);
      when(topicService.getTopicById(postSessionRequest.getTopicId())).thenReturn(Mono.just(topic));
      when(sessionRepository.save(any(Session.class))).thenReturn(Mono.error(new Exception()));
      
      StepVerifier.create(sessionService.createVoteSession(postSessionRequest))
          .expectError().verify();
      verifyNoInteractions(sessionProducer);
    }
    
    @Test
    void givenDefaultDurationSession_whenCreateVoteSession_thenShouldCreateDefaultSession() {
      var uuid = UUID.randomUUID().toString();
      var postSessionRequest = PostVoteSessionRequest.builder().topicId(uuid).build();
      var topic = new Topic("Test");
      var expectedSession = Session.create(postSessionRequest, topic);
      expectedSession.setId(UUID.randomUUID().toString());
      topic.setId(uuid);
      when(topicService.getTopicById(postSessionRequest.getTopicId())).thenReturn(Mono.just(topic));
      when(sessionRepository.save(any(Session.class))).thenReturn(Mono.just(expectedSession));
      
      StepVerifier.create(sessionService.createVoteSession(postSessionRequest))
          .expectNext(expectedSession.getId()).verifyComplete();
      verify(sessionProducer).postVoteSessionStarted(expectedSession);
      
      assertThat(expectedSession.getEndDate())
          .isEqualTo(expectedSession.getStartDate().plusMinutes(DEFAULT_DURATION));
    }
    
    @Test
    void givenCustomDurationSession_whenCreateVoteSession_thenShouldCreateCustomSession() {
      var uuid = UUID.randomUUID().toString();
      var postSessionRequest = PostVoteSessionRequest.builder()
          .topicId(uuid)
          .duration(5L)
          .build();
      var topic = new Topic("Test");
      var expectedSession = Session.create(postSessionRequest, topic);
      expectedSession.setId(UUID.randomUUID().toString());
      topic.setId(uuid);
      when(topicService.getTopicById(postSessionRequest.getTopicId())).thenReturn(Mono.just(topic));
      when(sessionRepository.save(any(Session.class))).thenReturn(Mono.just(expectedSession));
      
      StepVerifier.create(sessionService.createVoteSession(postSessionRequest))
          .expectNext(expectedSession.getId()).verifyComplete();
      verify(sessionProducer).postVoteSessionStarted(expectedSession);
      
      assertThat(expectedSession.getEndDate())
          .isNotEqualTo(expectedSession.getStartDate().plusMinutes(DEFAULT_DURATION));
    }
  }
  
  @Nested
  class SessionResultTest {
    
    @Test
    void givenValidSession_whenGetVotingSessionResult_thenReturnSessionResult() {
      var id = UUID.randomUUID().toString();
      var session = Session.builder()
          .topic("Test topic")
          .id(id)
          .build();
      when(sessionRepository.findById(anyString())).thenReturn(Mono.just(session));
      when(userVoteRepository.countAllBySessionIdAndVote(id, UserVoteEnum.YES))
          .thenReturn(Mono.just(10L));
      when(userVoteRepository.countAllBySessionIdAndVote(id, UserVoteEnum.NO))
          .thenReturn(Mono.just(5L));
      
      StepVerifier.create(sessionService.getVotingSessionResult(id))
          .assertNext(sessionResultResponse -> {
            Assertions.assertThat(sessionResultResponse.getId()).isEqualTo(session.getId());
            Assertions.assertThat(sessionResultResponse.getTopic()).isEqualTo(session.getTopic());
          }).verifyComplete();
    }
    
    @Test
    void givenNotValidSession_whenGetVotingSessionResult_thenThrowException() {
      var id = UUID.randomUUID().toString();
      when(sessionRepository.findById(anyString())).thenReturn(Mono.empty());
      when(userVoteRepository.countAllBySessionIdAndVote(id, UserVoteEnum.YES))
          .thenReturn(Mono.just(10L));
      when(userVoteRepository.countAllBySessionIdAndVote(id, UserVoteEnum.NO))
          .thenReturn(Mono.just(5L));
      
      StepVerifier.create(sessionService.getVotingSessionResult(id)).expectError().verify();
    }
  }
}
