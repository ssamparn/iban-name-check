package com.sparkle.demo.ibannamecheckblockingimpl.database.repository;

import com.sparkle.demo.ibannamecheckblockingimpl.database.relationship.FileRequestContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRequestContentRepository extends JpaRepository<FileRequestContentEntity, UUID> {

}
