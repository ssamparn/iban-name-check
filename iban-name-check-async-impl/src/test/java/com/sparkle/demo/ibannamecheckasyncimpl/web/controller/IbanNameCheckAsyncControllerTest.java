package com.sparkle.demo.ibannamecheckasyncimpl.web.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.sparkle.demo.ibannamecheckasyncimpl.IbanNameCheckAsyncImplApplication;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@AutoConfigureWebTestClient
@SpringBootTest(classes = IbanNameCheckAsyncImplApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IbanNameCheckAsyncControllerTest {

    private static WireMockServer wireMock = new WireMockServer(WireMockSpring.options()
            .port(7070)
            .notifier(new ConsoleNotifier(true))
            .extensions(new ResponseTemplateTransformer(true)));

    @Value("classpath:unit-test/pain/ct-pain-001.xml")
    private Resource painFile;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private IbanNameRepository ibanNameRepository;

    @BeforeAll
    static void setUpClass() {
        wireMock.start();
    }

    @BeforeEach
    public void beforeEach() {
        ibanNameRepository.deleteAll().block();
    }

    @Test
    void uploadPainFile() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("fileToUpload", painFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        webTestClient.post()
                .uri("/api/v1/upload-pain-file")
                .accept(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void uploadExcelFileMono() {
    }

    @Test
    void uploadExcelFileFlux() {
    }

    @AfterEach
    void tearDown() {
        wireMock.resetAll();
    }

    @AfterAll
    static void tearDownClass() {
        wireMock.shutdown();
    }
}