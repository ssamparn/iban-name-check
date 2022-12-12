package com.sparkle.demo.ibannamecheckapi.repository;

import com.sparkle.demo.ibannamecheckapi.document.IbanNameDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IbanNameRepository extends ReactiveMongoRepository<IbanNameDocument, String> {
    Mono<IbanNameDocument> findByAccountNumber(String accountNumber);
}
