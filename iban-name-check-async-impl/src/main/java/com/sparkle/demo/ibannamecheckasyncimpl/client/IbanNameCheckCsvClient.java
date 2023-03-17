package com.sparkle.demo.ibannamecheckasyncimpl.client;

import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Component
public class IbanNameCheckCsvClient {

    private final WebClient surePayClient;

    @Value("classpath:csv/file.csv")
    private Resource csvResource;

    @Autowired
    public IbanNameCheckCsvClient(@Qualifier("csvWebClient") WebClient surePayClient) {
        this.surePayClient = surePayClient;
    }

    public Mono<TaskResponse> uploadCsvFile(ByteArrayInputStream byteArrayInputStream, UUID requestId) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("csv", new InputStreamResource(byteArrayInputStream))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        return this.surePayClient
                .post()
                .uri("/upload")
                .accept(MediaType.TEXT_PLAIN)
                .headers(getHttpHeadersConsumer(requestId))
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .bodyToMono(TaskResponse.class);
    }

    public Flux<DataBuffer> downloadCsvFile(UUID taskId, ByteArrayInputStream byteArrayInputStream) {
        return this.surePayClient
                .post()
                .uri("/{taskId}/download", taskId)
                .body(Flux.concat(
                        FilePartEvent.create("csv", csvResource)
                ), PartEvent.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error: " + clientResponse.toString())))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error" + clientResponse.toString())))
                .bodyToFlux(DataBuffer.class);
    }

    private Consumer<HttpHeaders> getHttpHeadersConsumer(UUID requestId) {
        return httpHeaders -> {
            httpHeaders.setContentType(MediaType.TEXT_PLAIN);
            httpHeaders.set("X-Request-Id", requestId.toString());
        };
    }
}
