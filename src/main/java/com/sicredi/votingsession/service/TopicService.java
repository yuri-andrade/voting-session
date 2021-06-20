package com.sicredi.votingsession.service;


import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.controller.v1.request.CreateTopicRequest;
import com.sicredi.votingsession.domain.Topic;
import com.sicredi.votingsession.exception.NotFoundException;
import com.sicredi.votingsession.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicService {
  
  private final TopicRepository topicRepository;
  private final MessageError messageError;
  
  public Mono<String> createTopicRequest(CreateTopicRequest createTopicRequest) {
    return topicRepository.save(new Topic(createTopicRequest.getName()))
        .map(Topic::getId);
  }
  
  public Mono<Topic> getTopicById(String id) {
    return topicRepository.findById(id)
        .switchIfEmpty(Mono.defer(
            () -> Mono.error(new NotFoundException(messageError.create("topic.not.found")))));
  }
}
