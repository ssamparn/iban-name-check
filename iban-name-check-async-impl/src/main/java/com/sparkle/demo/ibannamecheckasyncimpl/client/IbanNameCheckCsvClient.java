package com.sparkle.demo.ibannamecheckasyncimpl.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;

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

    public Flux<DataBuffer> doPost(ByteArrayInputStream byteArrayInputStream) {
        return this.surePayClient
                .post()
                .uri("/check/banks/csv")
                .accept(MediaType.TEXT_PLAIN)
                .body(Flux.concat(
                        FilePartEvent.create("csv", csvResource)
                ), PartEvent.class)
                .retrieve()
                .bodyToFlux(DataBuffer.class);
    }
}
