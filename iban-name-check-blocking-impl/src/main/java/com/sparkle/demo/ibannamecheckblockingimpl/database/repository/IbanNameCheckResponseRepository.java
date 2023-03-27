package com.sparkle.demo.ibannamecheckblockingimpl.database.repository;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameCheckResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IbanNameCheckResponseRepository extends JpaRepository<IbanNameCheckResponseEntity, String> {
    List<IbanNameCheckResponseEntity> getAllByCorrelationId(UUID correlationId);
}
