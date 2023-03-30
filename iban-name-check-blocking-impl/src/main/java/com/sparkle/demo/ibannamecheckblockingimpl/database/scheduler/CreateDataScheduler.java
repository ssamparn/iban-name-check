package com.sparkle.demo.ibannamecheckblockingimpl.database.scheduler;

import com.sparkle.demo.ibannamecheckblockingimpl.database.relationship.FileRequestEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.FileRequestRepository;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CreateDataScheduler {

    @Autowired
    private FileRequestRepository fileRequestRepository;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(
            name = "create_data_scheduler",
            lockAtLeastFor = "PT1M",
            lockAtMostFor = "PT5M"
    )
    public void scheduledTask() {
        LockAssert.assertLocked();
        FileRequestEntity entity = new FileRequestEntity();
        entity.setRequestId(UUID.randomUUID().toString());
        entity.setTaskId(UUID.randomUUID().toString());
        fileRequestRepository.save(entity);
    }
}
