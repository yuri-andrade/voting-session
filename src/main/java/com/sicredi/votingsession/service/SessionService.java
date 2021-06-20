package com.sicredi.votingsession.service;

import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.controller.v1.request.PostVoteSessionRequest;
import com.sicredi.votingsession.controller.v1.response.SessionResultResponse;
import com.sicredi.votingsession.domain.Session;
import com.sicredi.votingsession.enums.UserVoteEnum;
import com.sicredi.votingsession.exception.NotFoundException;
import com.sicredi.votingsession.rabbitmq.producer.SessionProducer;
import com.sicredi.votingsession.repository.SessionRepository;
import com.sicredi.votingsession.repository.UserVoteRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
  
  private final TopicService topicService;
  private final SessionProducer sessionProducer;
  private final SessionRepository sessionRepository;
  private final UserVoteRepository userVoteRepository;
  private final MessageError messageError;
  
  public Mono<Session> getActiveVotingSession(String voteSessionId) {
    return sessionRepository.findByIdAndEndDateAfter(voteSessionId, LocalDateTime.now())
        .switchIfEmpty(Mono.defer(
            () -> Mono.error(new NotFoundException(messageError.create("session.not.found")))))
        .doOnError(error -> log.error("No active session was found with id: {}", voteSessionId));
  }
  
  public Mono<String> createVoteSession(PostVoteSessionRequest postVoteSessionRequest) {
    return topicService.getTopicById(postVoteSessionRequest.getTopicId())
        .map(topic -> Session.create(postVoteSessionRequest, topic))
        .flatMap(sessionRepository::save)
        .doOnSuccess(sessionProducer::postVoteSessionStarted)
        .map(Session::getId);
  }
  
  public Mono<SessionResultResponse> getVotingSessionResult(String id) {
    return Mono.zip(
        this.getById(id),
        userVoteRepository.countAllBySessionIdAndVote(id, UserVoteEnum.YES),
        userVoteRepository.countAllBySessionIdAndVote(id, UserVoteEnum.NO))
        .map(objects -> SessionResultResponse.builder()
            .id(objects.getT1().getId())
            .topic(objects.getT1().getTopic())
            .voteYes(objects.getT2())
            .voteNo(objects.getT3())
            .build());
  }
  
  public Mono<Session> getById(String id) {
    return sessionRepository.findById(id)
        .switchIfEmpty(Mono.defer(
            () -> Mono.error(new NotFoundException(messageError.create("session.not.found")))));
  }
}
