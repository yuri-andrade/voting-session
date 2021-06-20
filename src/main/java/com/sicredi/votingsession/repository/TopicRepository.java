package com.sicredi.votingsession.repository;

import com.sicredi.votingsession.domain.Topic;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TopicRepository extends ReactiveMongoRepository<Topic, String> {

}
