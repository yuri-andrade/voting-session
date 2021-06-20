package com.sicredi.votingsession.client;

import com.sicredi.votingsession.client.response.UserClientResponse;
import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.exception.UnprocessableEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserClient {
  
  
  private final String basePath;
  private final WebClient webClient;
  private final MessageError messageError;
  
  public UserClient(@Value("${client.user.url}") String basePath, MessageError messageError) {
    this.basePath = basePath;
    this.webClient = WebClient.create(basePath);
    this.messageError = messageError;
  }
  
  
  public Mono<UserClientResponse> getUserStatus(String cpf) {
    var uri = UriComponentsBuilder.fromUriString(basePath).pathSegment(cpf).toUriString();
    return webClient.get()
        .uri(uri)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono
            .error(new UnprocessableEntityException(messageError.create("user.invalid.cpf"))))
        .bodyToMono(UserClientResponse.class)
        .doOnError(throwable -> log.error("Error on external call: {}", uri))
        .doOnSuccess(s -> log.info("Success on external call: {}", uri));
  }
  
}
