package com.sicredi.votingsession.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sicredi.votingsession.client.response.UserClientResponse;
import com.sicredi.votingsession.configuration.MessageError.ApiError;
import com.sicredi.votingsession.controller.v1.request.PostVoteRequest;
import com.sicredi.votingsession.domain.Session;
import com.sicredi.votingsession.domain.UserVote;
import com.sicredi.votingsession.enums.UserVoteEnum;
import com.sicredi.votingsession.enums.UserVotingStatus;
import com.sicredi.votingsession.exception.NotFoundException;
import com.sicredi.votingsession.exception.UnprocessableEntityException;
import com.sicredi.votingsession.repository.UserVoteRepository;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {
  
  @Mock
  private UserVoteRepository userVoteRepository;
  @Mock
  private SessionService sessionService;
  @Mock
  private UserService userService;
  
  @InjectMocks
  private VoteService voteService;
  
  @Mock
  private UserVote userVote;
  
  @Nested
  class ComputeUserVoteTests {
    
    String uuid = UUID.randomUUID().toString();
    UserClientResponse ableToVoteResponse = UserClientResponse.builder()
        .status(UserVotingStatus.ABLE_TO_VOTE)
        .build();
    
    @Test
    void givenValidVoteRequest_whenComputeUserVote_shouldNotThrow() {
      PostVoteRequest postVoteRequest = PostVoteRequest.builder()
          .cpf("12345678912")
          .vote(UserVoteEnum.YES)
          .voteSessionId(UUID.randomUUID().toString())
          .build();
      Session session = Session.builder().build();
      
      when(userService.getUser(anyString())).thenReturn(Mono.just(ableToVoteResponse));
      when(sessionService.getActiveVotingSession(anyString())).thenReturn(Mono.just(session));
      when(userVote.getId()).thenReturn(uuid);
      when(userVoteRepository.save(any())).thenReturn(Mono.just(userVote));
      
      StepVerifier.create(voteService.computeUserVote(postVoteRequest))
          .expectNext(uuid)
          .expectComplete()
          .verify();
    }
    
    @Test
    void givenInvalidVoteSession_whenComputeUserVote_shouldThrow() {
      PostVoteRequest postVoteRequest = PostVoteRequest.builder()
          .cpf("12345678912")
          .vote(UserVoteEnum.YES)
          .voteSessionId(UUID.randomUUID().toString())
          .build();
      when(userService.getUser(anyString())).thenReturn(Mono.just(ableToVoteResponse));
      when(sessionService.getActiveVotingSession(anyString()))
          .thenReturn(Mono.error(new NotFoundException(new ApiError())));
      
      StepVerifier.create(voteService.computeUserVote(postVoteRequest)).expectError().verify();
       verifyNoInteractions(userVoteRepository);
    }
    
    @Test
    void givenInvalidUser_whenComputeUserVote_shouldThrow() {
      PostVoteRequest postVoteRequest = PostVoteRequest.builder()
          .cpf("12345678912")
          .vote(UserVoteEnum.YES)
          .voteSessionId(UUID.randomUUID().toString())
          .build();
      when(userService.getUser(anyString()))
          .thenReturn(Mono.error(new UnprocessableEntityException(new ApiError())));
      
      StepVerifier.create(voteService.computeUserVote(postVoteRequest)).expectError().verify();
      verifyNoInteractions(userVoteRepository, sessionService);
    }
  }
}