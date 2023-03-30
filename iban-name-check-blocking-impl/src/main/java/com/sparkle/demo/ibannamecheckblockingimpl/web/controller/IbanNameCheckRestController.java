package com.sparkle.demo.ibannamecheckblockingimpl.web.controller;

import com.sparkle.demo.ibannamecheckblockingimpl.business.IbanNameCheckBusinessImpl;
import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameResponseEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskResponse;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.sparkle.demo.ibannamecheckblockingimpl.web.util.ResponseHeaderUtil.excelResponseHeaders;
import static com.sparkle.demo.ibannamecheckblockingimpl.web.util.ResponseHeaderUtil.jsonResponseHeaders;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IbanNameCheckRestController {

    private final IbanNameCheckBusinessImpl ibanNameCheckBusiness;

    @PostMapping(value = "/ancs-upload-pain", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<TaskResponse>> uploadPain(@RequestPart("file") Mono<FilePart> filePartMono) {
        log.info("requesting upload endpoint of ancs");
        return this.ibanNameCheckBusiness.uploadPainFile(filePartMono)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );
    }

    @PostMapping(value = "/ancs-upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> uploadExcel(@RequestPart("file") Mono<FilePart> filePartMono) {
        final UUID correlationId = UUID.randomUUID();
        return this.ibanNameCheckBusiness.uploadExcelFile(filePartMono, correlationId)
                .map(InputStreamResource::new)
                .map(inputStreamResource -> ResponseEntity.ok()
                        .headers(excelResponseHeaders(correlationId.toString()))
                        .body(inputStreamResource)
                );
    }

    @PostMapping(value = "/ancs-upload-excel-relationship", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<TaskResponse>> uploadExcelRelationships(@RequestPart("file") Mono<FilePart> filePartMono) {
        final UUID requestId = UUID.randomUUID();
        return this.ibanNameCheckBusiness.uploadExcelFileRelationship(filePartMono, requestId)
                .flatMap(ibanNameCheckBusiness::updateTaskId)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );
    }

    @GetMapping(value = "/ancs-upload/{taskId}/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TaskStatusResponse> checkTaskStatus(@PathVariable("taskId") UUID taskId) {
        log.info("requesting task status check endpoint of ancs");
        return ibanNameCheckBusiness.checkUploadStatus(taskId);
    }

    @PostMapping(value = "/ancs-download/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> download(@RequestPart("file") Mono<FilePart> filePartMono,
                                                       @PathVariable("taskId") UUID taskId) {
        log.info("requesting download csv endpoint of ancs");
        return this.ibanNameCheckBusiness.downloadTask(filePartMono, taskId)
                .map(InputStreamResource::new)
                .map(inputStreamResource -> ResponseEntity.ok()
                        .headers(excelResponseHeaders(taskId.toString()))
                        .body(inputStreamResource));
    }

    // experimental feature
    @GetMapping(value = "/get-updated-status/{correlationId}")
    public Mono<ResponseEntity<List<IbanNameResponseEntity>>> getUpdatedStatus(@PathVariable("correlationId") String correlationId) {
        return this.ibanNameCheckBusiness.downloadStatus(correlationId)
                .collectList()
                .map(list -> ResponseEntity.ok().headers(jsonResponseHeaders(correlationId)).body(list));
    }

    @PostMapping(value = "/upload-excel-file-flux", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<List<IbanNameModel>> uploadExcelFileFlux(@RequestPart("fileToUpload") Flux<FilePart> filePartFlux) {
        return this.ibanNameCheckBusiness.multiUploadExcelFile(filePartFlux);
    }
}
