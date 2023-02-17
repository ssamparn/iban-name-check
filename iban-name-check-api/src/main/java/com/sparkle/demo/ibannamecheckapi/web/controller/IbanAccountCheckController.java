package com.sparkle.demo.ibannamecheckapi.web.controller;

import com.sparkle.demo.ibannamecheckapi.service.IbanNameService;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/v2/account")
@RequiredArgsConstructor
public class IbanAccountCheckController {

    String fileName = String.format("%s.csv", RandomStringUtils.randomAlphabetic(10));

    private final IbanNameService ibanNameService;

    @PostMapping(value = "/check/banks/json", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<ResponseEntity<IbanAccountCheckResponse>> checkAccountJson(@RequestBody Mono<IbanAccountCheckRequest> accountCheckRequestMono) {
        log.info("requesting IbanAccountCheckController");

        return this.ibanNameService.doJsonPayloadCheck(accountCheckRequestMono)
                .map(ResponseEntity::ok);
    }


    @PostMapping(value = "/check/banks/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> checkAccountCsv(@RequestPart("csv") Mono<FilePart> filePartMono) {
        log.info("requesting IbanAccountCheckController");

        return this.ibanNameService.doCsvPayloadCheck(filePartMono)
                .map(InputStreamResource::new)
                .map(inputStreamResource -> ResponseEntity.ok().headers(responseHeaders()).body(inputStreamResource));
    }

    private HttpHeaders responseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return responseHeaders;
    }
}
