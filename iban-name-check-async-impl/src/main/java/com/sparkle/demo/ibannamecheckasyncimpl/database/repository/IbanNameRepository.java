package com.sparkle.demo.ibannamecheckasyncimpl.database.repository;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface IbanNameRepository extends ReactiveCrudRepository<IbanNameEntity, UUID> {
    Flux<IbanNameEntity> getIbanNameEntitiesByCorrelationId(UUID correlationId);
}
