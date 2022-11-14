package com.sparkle.demo.ibannamecheckasyncimpl.web.controller;

import com.sparkle.demo.ibannamecheckasyncimpl.service.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.service.FilePartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IbanNameCheckAsyncController {

    private final FileMapper fileMapper;
    private final FilePartService filePartService;

    @PostMapping(value = "/upload-pain-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFile(@RequestPart("fileToUpload") Mono<FilePart> filePartMono,
                                   @RequestHeader("Content-Length") long contentLength) {

        return filePartMono
                .doOnNext(fp -> System.out.println("Received File Size : " + contentLength + " bytes"))
                .doOnNext(fp -> System.out.println("Received File : " + fp.filename()))
                .mapNotNull(fp -> {
                    try {
                        return filePartService.upload(fp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).switchIfEmpty(Mono.just("empty"));
    }

}
