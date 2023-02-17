package com.sparkle.demo.ibannamecheckasyncimpl.client;

import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class IbanNameCheckJsonClient {

    private final WebClient surePayClient;

    @Autowired
    public IbanNameCheckJsonClient(@Qualifier("jsonWebClient") WebClient surePayClient) {
        this.surePayClient = surePayClient;
    }

    public Mono<IbanNameCheckResponse> doPost(IbanNameCheckRequest request) {
        return this.surePayClient
                .post()
                .uri("/check/banks/json")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(IbanNameCheckResponse.class);
    }
}
