package com.sparkle.demo.ibannamecheckapi.web.controller;

import com.sparkle.demo.ibannamecheckapi.service.IbanNameService;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/v2/account")
@RequiredArgsConstructor
public class IbanAccountCheckController {

    private final IbanNameService ibanNameService;

    @PostMapping(value = "/check/banks", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<ResponseEntity<IbanAccountCheckResponse>> accountCheck(@RequestBody Mono<IbanAccountCheckRequest> accountCheckRequestMono) {
        log.info("requesting IbanAccountCheckController");

        return this.ibanNameService.doAccountCheck(accountCheckRequestMono)
                .map(ResponseEntity::ok);
    }
}
