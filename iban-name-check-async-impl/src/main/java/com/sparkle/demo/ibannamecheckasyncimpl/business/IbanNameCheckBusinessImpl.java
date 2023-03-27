package com.sparkle.demo.ibannamecheckasyncimpl.business;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameResponseEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.service.excel.ExcelFileService;
import com.sparkle.demo.ibannamecheckasyncimpl.service.pain.PainFileService;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.TaskResponse;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.TaskStatusResponse;
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

    private final PainFileService painFileService;
    private final ExcelFileService excelFileService;

    public Mono<TaskResponse> uploadPainFile(final Mono<FilePart> filePartMono) {
        final UUID requestId = UUID.randomUUID();
        return this.painFileService.processPainFile(filePartMono, requestId);
    }

    public Mono<ByteArrayInputStream> uploadExcelFile(final Mono<FilePart> filePartMono, final UUID requestId) {
        return this.excelFileService.processExcelFile(filePartMono, requestId);
    }

    public Flux<TaskStatusResponse> checkUploadStatus(final UUID taskId) {
        return this.painFileService.checkTaskStatus(taskId);
    }

    public Mono<ByteArrayInputStream> downloadTask(final Mono<FilePart> filePartMono, final UUID taskId) {
        return this.painFileService.processCsvDownload(filePartMono, taskId);
    }

    // experimental feature
    public Flux<List<IbanNameModel>> multiUploadExcelFile(final Flux<FilePart> filePartFlux) {
        return this.excelFileService.processMultipleExcelFile(filePartFlux);
    }

    public Flux<IbanNameResponseEntity> downloadStatus(String correlationId) {
        return this.painFileService.downloadUpdatedStatus(correlationId);
    }
}
