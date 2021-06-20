package com.sicredi.votingsession.service;

import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.controller.v1.request.PostVoteRequest;
import com.sicredi.votingsession.domain.UserVote;
import com.sicredi.votingsession.exception.UnprocessableEntityException;
import com.sicredi.votingsession.repository.UserVoteRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {
  
  private final UserVoteRepository userVoteRepository;
  private final SessionService sessionService;
  private final UserService userService;
  private final MessageError messageError;
  
  public Mono<String> computeUserVote(PostVoteRequest postVoteRequest) {
    return userService.getUser(postVoteRequest.getCpf())
        .flatMap(userClientResponse -> sessionService.getActiveVotingSession(postVoteRequest.getVoteSessionId()))
        .flatMap(voteSession -> this.saveUserVote(postVoteRequest))
        .map(UserVote::getId);
  }
  
  public Mono<UserVote> saveUserVote(PostVoteRequest postVoteRequest) {
    var userVote = new UserVote(postVoteRequest.getCpf(), postVoteRequest.getVoteSessionId(),
        LocalDateTime.now(), postVoteRequest.getVote());
    return userVoteRepository.save(userVote)
        .doOnSuccess(it -> log.info("Success saving user: {} vote on session: {}", postVoteRequest.getCpf(), postVoteRequest.getVoteSessionId()))
        .doOnError(it -> log.error("Error saving user: {} vote on session: {} message:{}", postVoteRequest.getCpf(), postVoteRequest.getVoteSessionId(), it.getMessage()))
        .onErrorResume(throwable -> Mono.error(new UnprocessableEntityException(messageError.create("user.duplicated.vote"))));
  }
  
}
