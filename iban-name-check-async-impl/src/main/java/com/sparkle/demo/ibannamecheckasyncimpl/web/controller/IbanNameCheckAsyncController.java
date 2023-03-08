package com.sparkle.demo.ibannamecheckasyncimpl.web.controller;

import com.sparkle.demo.ibannamecheckasyncimpl.business.IbanNameCheckBusinessImpl;
import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
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

import static com.sparkle.demo.ibannamecheckasyncimpl.web.util.ResponseHeaderUtil.jsonResponseHeaders;
import static com.sparkle.demo.ibannamecheckasyncimpl.web.util.ResponseHeaderUtil.excelResponseHeaders;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IbanNameCheckAsyncController {

    private final IbanNameCheckBusinessImpl ibanNameCheckBusiness;

    @PostMapping(value = "/upload-pain-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> uploadPainFile(@RequestPart("fileToUpload") Mono<FilePart> filePartMono) {
        final UUID correlationId = UUID.randomUUID();
        return this.ibanNameCheckBusiness.uploadPainFile(filePartMono, correlationId)
                .map(InputStreamResource::new)
                .map(inputStreamResource -> ResponseEntity.ok().headers(excelResponseHeaders(correlationId.toString())).body(inputStreamResource));
    }

    @PostMapping(value = "/upload-excel-file-mono", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> uploadExcelFileMono(@RequestPart("fileToUpload") Mono<FilePart> filePartMono) {
        final UUID correlationId = UUID.randomUUID();
        return this.ibanNameCheckBusiness.uploadExcelFileAsMono(filePartMono)
                .map(InputStreamResource::new)
                .map(inputStreamResource -> ResponseEntity.ok().headers(excelResponseHeaders(correlationId.toString())).body(inputStreamResource));
    }

    @GetMapping(value = "/get-updated-status/{correlationId}")
    public Mono<ResponseEntity<List<IbanNameCheckResponseEntity>>> getUpdatedStatus(@PathVariable("correlationId") String correlationId) {
        return this.ibanNameCheckBusiness.downloadStatus(correlationId)
                .collectList()
                .map(list -> ResponseEntity.ok().headers(jsonResponseHeaders(correlationId)).body(list));
    }

    @PostMapping(value = "/upload-excel-file-flux", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<List<IbanNameModel>> uploadExcelFileFlux(@RequestPart("fileToUpload") Flux<FilePart> filePartFlux) {
        return this.ibanNameCheckBusiness.uploadExcelFileAsFlux(filePartFlux);
    }

    @PostMapping(value = "/get-mime-type", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> getMimeType(@RequestPart("fileToUpload") Mono<FilePart> filePartMono) {
        return this.ibanNameCheckBusiness.getRealMimeType(filePartMono);
    }
}
