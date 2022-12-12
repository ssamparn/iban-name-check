package com.sparkle.demo.ibannamecheckasyncimpl.repository;

import com.sparkle.demo.ibannamecheckasyncimpl.document.IbanEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IbanNameRepository extends ReactiveMongoRepository<IbanEntity, String> {

}
