package com.sparkle.demo.ibannamecheckapi.web.controller;

import com.sparkle.demo.ibannamecheckapi.service.IbanNameService;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.TaskIdResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.TaskStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/account")
@RequiredArgsConstructor
public class IbanAccountCheckController {

    private String fileName = String.format("%s.csv", RandomStringUtils.randomAlphabetic(10));

    private final IbanNameService ibanNameService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<TaskIdResponse>> upload(@RequestPart("csv") Mono<FilePart> filePartMono,
                                                       @RequestHeader("X-Request-Id") UUID xRequestId) {
        log.info("requesting upload endpoint of sure pay");
        return this.ibanNameService.createTaskIdResponse(filePartMono, xRequestId)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response))
                .doOnNext(response -> log.info("task response taskId: {} and requestId: {} ", response.getBody().getTaskId(), response.getBody().getXRequestId()));
    }

    @GetMapping(value = "/{taskId}/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TaskStatusResponse> checkStatus(@PathVariable("taskId") UUID taskId) {
        log.info("requesting the task status check endpoint of sure pay");

        return Flux.interval(Duration.ofSeconds(1))
                .onBackpressureDrop()
                .map(ibanNameService::createTaskStatusResponse)
                .flatMapIterable(statusResponses -> statusResponses)
                .doOnNext(response -> log.info("Emitted status: {}", response));
    }

    @PostMapping(value = "/{taskId}/download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> checkAccountCsv(@RequestPart("csv") Mono<FilePart> filePartMono,
                                                          @PathVariable("taskId") UUID taskId) {
        log.info("requesting the csv download endpoint of sure pay");

        return this.ibanNameService.doCsvPayloadCheck(filePartMono)
                .map(InputStreamResource::new)
                .map(inputStreamResource -> ResponseEntity.ok().headers(responseHeaders()).body(inputStreamResource));
    }

    @PostMapping(value = "/check/banks/json", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<ResponseEntity<IbanAccountCheckResponse>> checkAccountJson(@RequestBody Mono<IbanAccountCheckRequest> accountCheckRequestMono) {
        log.info("requesting IbanAccountCheckController");

        return this.ibanNameService.doJsonPayloadCheck(accountCheckRequestMono)
                .map(ResponseEntity::ok);
    }

    private HttpHeaders responseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return responseHeaders;
    }
}
