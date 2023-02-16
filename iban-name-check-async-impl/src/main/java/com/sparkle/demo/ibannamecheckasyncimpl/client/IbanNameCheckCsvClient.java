package com.sparkle.demo.ibannamecheckasyncimpl.client;

import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class IbanNameCheckCsvClient {

    private final WebClient surePayClient;

    @Autowired
    public IbanNameCheckCsvClient(WebClient surePayClient) {
        this.surePayClient = surePayClient;
    }

    public Mono<IbanNameCheckResponse> doPost(InputStreamResource inputStreamResource) {

        MultipartBodyBuilder csvBodyBuilder = new MultipartBodyBuilder();
        csvBodyBuilder.part("csv", inputStreamResource);

        return this.surePayClient
                .post()
                .uri("/check/banks/csv")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(csvBodyBuilder.build()))
                .retrieve()
                .bodyToMono(IbanNameCheckResponse.class);
    }
}