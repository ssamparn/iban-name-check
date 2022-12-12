package com.sparkle.demo.ibannamecheckapi.web.controller;

import com.sparkle.demo.ibannamecheckapi.service.IbanNameService;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v2/account")
@RequiredArgsConstructor
public class IbanAccountCheckController {

    private final IbanNameService ibanNameService;

    @PostMapping(value = "/check/banks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<IbanAccountCheckResponse> accountCheck(@RequestBody Mono<IbanAccountCheckRequest> accountCheckRequestMono,
                                                       @RequestHeader(name = "X-Correlation-Id") String xCorrelationId) {

        return ibanNameService.doAccountCheck(accountCheckRequestMono, xCorrelationId);
    }
}
