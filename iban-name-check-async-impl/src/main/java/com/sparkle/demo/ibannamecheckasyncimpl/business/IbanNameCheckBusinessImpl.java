package com.sparkle.demo.ibannamecheckasyncimpl.business;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
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

    public Mono<TaskResponse> uploadPainFile(Mono<FilePart> filePartMono, UUID requestId) {
        return this.painFileService.processPainFile(filePartMono, requestId);
    }

    public Mono<ByteArrayInputStream> uploadExcelFile(Mono<FilePart> filePartMono, UUID requestId) {
        return this.excelFileService.processExcelFile(filePartMono, requestId);
    }

    public Flux<TaskStatusResponse> checkUploadStatus(UUID taskId) {
        return this.painFileService.checkTaskStatus(taskId);
    }

    public Mono<ByteArrayInputStream> downloadTask(Mono<FilePart> filePartMono, UUID taskId) {
        return this.painFileService.processCsvDownload(filePartMono, taskId);
    }

    // experimental feature
    public Flux<List<IbanNameModel>> multiUploadExcelFile(Flux<FilePart> filePartFlux) {
        return this.excelFileService.processMultipleExcelFile(filePartFlux);
    }

    public Flux<IbanNameCheckResponseEntity> downloadStatus(String correlationId) {
        return this.painFileService.downloadUpdatedStatus(correlationId);
    }
}
