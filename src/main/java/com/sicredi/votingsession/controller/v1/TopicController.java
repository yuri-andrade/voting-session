package com.sicredi.votingsession.controller.v1;


import com.sicredi.votingsession.controller.v1.request.CreateTopicRequest;
import com.sicredi.votingsession.controller.v1.response.Response;
import com.sicredi.votingsession.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/v1/topic")
@RestController
@RequiredArgsConstructor
public class TopicController {
  
  private final TopicService topicService;
  
  @PostMapping
  public Mono<Response> postTopic(@RequestBody CreateTopicRequest createTopicRequest) {
    return topicService.createTopicRequest(createTopicRequest).map(Response::new);
  }
}
