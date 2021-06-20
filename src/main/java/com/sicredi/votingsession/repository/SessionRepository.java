package com.sicredi.votingsession.repository;

import com.sicredi.votingsession.domain.Session;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SessionRepository extends ReactiveMongoRepository<Session, String> {
  
  Mono<Session> findByIdAndEndDateAfter(String id, LocalDateTime endDate);
  
}
