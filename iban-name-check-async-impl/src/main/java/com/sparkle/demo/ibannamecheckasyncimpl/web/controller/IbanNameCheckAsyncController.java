package com.sparkle.demo.ibannamecheckasyncimpl.web.controller;

import com.sparkle.demo.ibannamecheckasyncimpl.business.IbanNameCheckBusinessImpl;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IbanNameCheckAsyncController {

    private final IbanNameCheckBusinessImpl ibanNameCheckBusiness;

    @PostMapping(value = "/upload-pain-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> uploadPainFile(@RequestPart("fileToUpload") Mono<FilePart> filePartMono,
                                     @RequestHeader("Content-Length") long contentLength) {
        return this.ibanNameCheckBusiness.uploadPainFile(filePartMono)
                .map(InputStreamResource::new)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/upload-excel-file-flux", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<List<IbanNameModel>> uploadExcelFileFlux(@RequestPart("fileToUpload") Flux<FilePart> filePartFlux) {
        return this.ibanNameCheckBusiness.uploadExcelFileAsFlux(filePartFlux);
    }

    @PostMapping(value = "/upload-excel-file-mono", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<IbanNameModel>>> uploadExcelFileMono(@RequestPart("fileToUpload") Mono<FilePart> filePartMono) {
        return this.ibanNameCheckBusiness.uploadExcelFileAsMono(filePartMono)
                .map(ResponseEntity::ok);
    }
}
