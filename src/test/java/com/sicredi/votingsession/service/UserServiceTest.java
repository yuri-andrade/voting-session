package com.sicredi.votingsession.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.sicredi.votingsession.client.UserClient;
import com.sicredi.votingsession.client.response.UserClientResponse;
import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.configuration.MessageError.ApiError;
import com.sicredi.votingsession.enums.UserVotingStatus;
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
class UserServiceTest {
  
  @Mock
  private UserClient userClient;
  @Mock
  private MessageError messageError;
  
  @InjectMocks
  private UserService userService;
  
  @Nested
  class GetUserTest {
    
    UserClientResponse ableToVoteResponse = UserClientResponse.builder()
        .status(UserVotingStatus.ABLE_TO_VOTE)
        .build();
    
    UserClientResponse unableToVoteResponse = UserClientResponse.builder()
        .status(UserVotingStatus.UNABLE_TO_VOTE)
        .build();
    
    @Test
    void givenUserAbleToVote_whenGetUser_thenDoesntThrow() {
      when(userClient.getUserStatus(anyString())).thenReturn(Mono.just(ableToVoteResponse));
      
      StepVerifier.create(userService.getUser("123")).expectNext(ableToVoteResponse)
          .verifyComplete();
      
    }
    
    @Test
    void givenUserUnableToVote_whenGetUser_thenThrow() {
      when(userClient.getUserStatus(anyString())).thenReturn(Mono.just(unableToVoteResponse));
      when(messageError.create(anyString())).thenReturn(new ApiError());
      
      StepVerifier.create(userService.getUser("123")).verifyError();
      
    }
  }
  
}
