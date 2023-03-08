package com.sparkle.demo.ibannamecheckasyncimpl.database.repository;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface IbanNameCheckResponseRepository extends ReactiveCrudRepository<IbanNameCheckResponseEntity, String> {
    Flux<IbanNameCheckResponseEntity> getAllByCorrelationId(UUID correlationId);
}
