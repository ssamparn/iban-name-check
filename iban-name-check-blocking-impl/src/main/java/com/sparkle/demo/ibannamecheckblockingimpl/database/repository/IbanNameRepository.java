package com.sparkle.demo.ibannamecheckblockingimpl.database.repository;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IbanNameRepository extends JpaRepository<IbanNameRequestEntity, UUID> {

}
