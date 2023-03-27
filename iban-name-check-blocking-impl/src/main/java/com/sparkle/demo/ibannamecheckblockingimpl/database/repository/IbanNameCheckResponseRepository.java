package com.sparkle.demo.ibannamecheckblockingimpl.database.repository;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IbanNameCheckResponseRepository extends JpaRepository<IbanNameResponseEntity, String> {
    List<IbanNameResponseEntity> getAllByCorrelationId(UUID correlationId);
}
