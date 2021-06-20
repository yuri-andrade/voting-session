package com.sicredi.votingsession.controller.v1;

import com.sicredi.votingsession.controller.v1.request.PostVoteSessionRequest;
import com.sicredi.votingsession.controller.v1.response.Response;
import com.sicredi.votingsession.controller.v1.response.SessionResultResponse;
import com.sicredi.votingsession.service.SessionService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/session")
@RequiredArgsConstructor
public class SessionController {
  
  private final SessionService sessionService;
  
  @PostMapping
  public Mono<Response> createVotingSession(
      @Valid @RequestBody PostVoteSessionRequest postVoteSessionRequest) {
    return sessionService.createVoteSession(postVoteSessionRequest).map(Response::new);
  }
  
  @GetMapping("/result/{id}")
  public Mono<SessionResultResponse> getVotingSessionResult(@PathVariable String id) {
    return sessionService.getVotingSessionResult(id);
  }
}
