package com.sicredi.votingsession.repository;

import com.sicredi.votingsession.domain.UserVote;
import com.sicredi.votingsession.enums.UserVoteEnum;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserVoteRepository extends ReactiveMongoRepository<UserVote, String> {
  
  Flux<UserVote> findAllBySessionId(String sessionId);
  
  Mono<Long> countAllBySessionIdAndVote(String sessionId, UserVoteEnum vote);

}
