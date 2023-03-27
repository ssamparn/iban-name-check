package com.sparkle.demo.ibannamecheckblockingimpl.database.repository;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IbanNameRepository extends JpaRepository<IbanNameEntity, UUID> {

}
