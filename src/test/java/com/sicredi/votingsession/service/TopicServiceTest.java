package com.sicredi.votingsession.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sicredi.votingsession.configuration.MessageError;
import com.sicredi.votingsession.configuration.MessageError.ApiError;
import com.sicredi.votingsession.controller.v1.request.CreateTopicRequest;
import com.sicredi.votingsession.domain.Topic;
import com.sicredi.votingsession.repository.TopicRepository;
import java.util.UUID;
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
class TopicServiceTest {
  
  @Mock
  private TopicRepository topicRepository;
  @Mock
  private MessageError messageError;
  @InjectMocks
  private TopicService topicService;
  
  @Nested
  class PostTopicTest {
    
    @Test
    void givenValidCreateTopicRequest_whenCreateTopicRequest_thenDoesntThrow() {
      var uuid = UUID.randomUUID().toString();
      var createTopicRequest = CreateTopicRequest.builder().name("Test topic")
          .build();
      var expectedTopic = new Topic(createTopicRequest.getName());
      expectedTopic.setId(uuid);
      Mockito.when(topicRepository.save(any(Topic.class)))
          .thenReturn(Mono.just(expectedTopic));
      StepVerifier.create(topicService.createTopicRequest(createTopicRequest)).expectNext(uuid)
          .verifyComplete();
      
      verify(topicRepository).save(any());
    }
  }
  
  @Nested
  class GetTopicTest {
    
    @Test
    void givenValidTopicId_whenGetTopicById_thenReturnTopic() {
      var testTopic = new Topic("Test");
      when(topicRepository.findById(anyString())).thenReturn(Mono.just(testTopic));
      
      StepVerifier.create(topicService.getTopicById(anyString()))
          .expectNext(testTopic)
          .verifyComplete();
    }
    
    @Test
    void givenInvalidTopicId_whenGetTopicById_thenThrow() {
      when(topicRepository.findById(anyString()))
          .thenReturn(Mono.empty());
      when(messageError.create(anyString())).thenReturn(new ApiError());
      
      StepVerifier.create(topicService.getTopicById(anyString())).verifyError();
    }
  }
}
