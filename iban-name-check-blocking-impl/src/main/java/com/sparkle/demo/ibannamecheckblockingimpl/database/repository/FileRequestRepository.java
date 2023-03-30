package com.sparkle.demo.ibannamecheckblockingimpl.database.repository;

import com.sparkle.demo.ibannamecheckblockingimpl.database.relationship.FileRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRequestRepository extends JpaRepository<FileRequestEntity, UUID> {

    @Query("select fr from file_request fr WHERE fr.requestId = :requestId")
    FileRequestEntity findByRequestId(@Param("requestId") String requestId);

    @Modifying
    @Query("update file_request fr SET fr.taskId = :taskId WHERE fr.requestId = :requestId")
    FileRequestEntity updateTaskId(@Param("taskId") String taskId, @Param("requestId") String reqestId);
}
