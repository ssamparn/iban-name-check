package com.sparkle.demo.ibannamecheckasyncimpl.web.controller;

import com.sparkle.demo.ibannamecheckasyncimpl.service.handler.excel.ExcelFileService;
import com.sparkle.demo.ibannamecheckasyncimpl.service.handler.pain.PainFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IbanNameCheckAsyncController {

    private final PainFileService painFileService;
    private final ExcelFileService excelFileService;

    @PostMapping(value = "/upload-pain-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> uploadFile(@RequestPart("fileToUpload") Mono<FilePart> filePartMono,
                                     @RequestHeader("Content-Length") long contentLength) {
        return this.painFileService.uploadFile(filePartMono)
                .map(InputStreamResource::new)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/upload-excel-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<String> uploadFile(@RequestPart("fileToUpload") Flux<FilePart> filePartFlux) {
        return excelFileService.uploadFile(filePartFlux);
    }

}
