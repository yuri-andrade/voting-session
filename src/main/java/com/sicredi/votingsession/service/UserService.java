package com.sicredi.votingsession.service;

import com.sicredi.votingsession.client.UserClient;
import com.sicredi.votingsession.client.response.UserClientResponse;
import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.enums.UserVotingStatus;
import com.sicredi.votingsession.exception.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
  
  private final UserClient userClient;
  private final MessageError messageError;
  
  public Mono<UserClientResponse> getUser(String cpf) {
    return userClient.getUserStatus(cpf)
        .filter(userClientResponse -> UserVotingStatus.ABLE_TO_VOTE
            .equals(userClientResponse.getStatus()))
        .switchIfEmpty(Mono.defer(() -> Mono.error(
            new UnprocessableEntityException(messageError.create("user.not.able.to.vote")))));
  }
}
