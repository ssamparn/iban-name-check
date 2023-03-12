package com.sparkle.demo.ibannamecheckasyncimpl.client;

import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.TaskStatusResponse;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class IbanNameCheckJsonClient {

    private final WebClient surePayClient;

    @Autowired
    public IbanNameCheckJsonClient(@Qualifier("jsonWebClient") WebClient surePayClient) {
        this.surePayClient = surePayClient;
    }

    public Flux<TaskStatusResponse> getUpdatedTaskStatus(UUID taskId) {
        return this.surePayClient
                .get()
                .uri("/{taskId}/status", taskId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .bodyToFlux(TaskStatusResponse.class)
                .doOnNext(emittedStatus -> log.info("Received : {}", emittedStatus));
    }

    public Mono<IbanNameCheckResponse> postJsonPayload(IbanNameCheckRequest request) {
        return this.surePayClient
                .post()
                .uri("/check/banks/json")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .bodyToMono(IbanNameCheckResponse.class);
    }
}
