package com.sparkle.demo.ibannamecheckblockingimpl.business;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameResponseEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.service.processor.excel.ExcelFileProcessor;
import com.sparkle.demo.ibannamecheckblockingimpl.service.processor.pain.PainFileProcessor;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskResponse;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IbanNameCheckBusinessImpl {

    private final PainFileProcessor painFileProcessor;
    private final ExcelFileProcessor excelFileProcessor;

    public Mono<TaskResponse> uploadPainFile(final Mono<FilePart> filePartMono) {
        final UUID requestId = UUID.randomUUID();
        return this.painFileProcessor.processPainFile(filePartMono, requestId);
    }

    public Mono<ByteArrayInputStream> uploadExcelFile(final Mono<FilePart> filePartMono, final UUID requestId) {
        return this.excelFileProcessor.processExcelFile(filePartMono, requestId);
    }

    public Flux<TaskStatusResponse> checkUploadStatus(final UUID taskId) {
        return this.painFileProcessor.checkTaskStatus(taskId);
    }

    public Mono<ByteArrayInputStream> downloadTask(final Mono<FilePart> filePartMono, final UUID taskId) {
        return this.painFileProcessor.processCsvDownload(filePartMono, taskId);
    }

    // experimental feature
    public Flux<List<IbanNameModel>> multiUploadExcelFile(final Flux<FilePart> filePartFlux) {
        return this.excelFileProcessor.processMultipleExcelFile(filePartFlux);
    }

    public Flux<IbanNameResponseEntity> downloadStatus(final String correlationId) {
        return this.painFileProcessor.downloadUpdatedStatus(correlationId);
    }
}
