package com.sicredi.votingsession.controller.v1;

import com.sicredi.votingsession.controller.v1.request.PostVoteRequest;
import com.sicredi.votingsession.controller.v1.response.Response;
import com.sicredi.votingsession.service.VoteService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/v1/vote")
@RestController
@RequiredArgsConstructor
public class VoteController {
  
  private final VoteService voteService;
  
  @PostMapping
  public Mono<Response> postVoteRequest(@Valid @RequestBody PostVoteRequest postVoteRequest) {
    return voteService.computeUserVote(postVoteRequest).map(Response::new);
  }
}
