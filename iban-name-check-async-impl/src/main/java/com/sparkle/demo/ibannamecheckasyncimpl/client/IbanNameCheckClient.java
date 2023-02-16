package com.sparkle.demo.ibannamecheckasyncimpl.client;

import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class IbanNameCheckClient {

    private final WebClient surePayClient;

    @Autowired
    public IbanNameCheckClient(WebClient surePayClient) {
        this.surePayClient = surePayClient;
    }

    public Mono<IbanNameCheckResponse> postJsonPayload(IbanNameCheckRequest request) {
        Mono<IbanNameCheckResponse> ibanNameCheckResponseMono = this.surePayClient
                .post()
                .uri("/check/banks")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(IbanNameCheckResponse.class);

        return ibanNameCheckResponseMono;
    }

    public Mono<IbanNameCheckResponse> postFilePayload(InputStreamResource inputStreamResource) {
        return this.surePayClient
                .post()
                .uri("/check/banks")
                .contentType(new MediaType("text", "csv"))
                .body(BodyInserters.fromResource(inputStreamResource))
                .retrieve()
                .bodyToMono(IbanNameCheckResponse.class);

    }
}
