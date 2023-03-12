//package com.sparkle.demo.ibannamecheckasyncimpl.business;
//
//import com.sparkle.demo.ibannamecheckasyncimpl.service.excel.ExcelFileService;
//import com.sparkle.demo.ibannamecheckasyncimpl.service.pain.PainFileService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@Slf4j
//@ExtendWith(SpringExtension.class)
//class IbanNameCheckBusinessImplTest {
//
//    private ByteArrayInputStream byteArrayInputStream;
//    private ByteArrayOutputStream byteArrayOutputStream;
//
//    @Value("classpath:unit-test/excel/file.xlsx")
//    private Resource excelFile;
//
//    @Mock
//    private FilePart filePartMock;
//
//    @Mock
//    private PainFileService painFileServiceMock;
//
//    @Mock
//    private ExcelFileService excelFileServiceMock;
//
//    @InjectMocks
//    private IbanNameCheckBusinessImpl ibanNameCheckBusiness;
//
//    @BeforeEach
//    void setUp() throws IOException {
//         byteArrayOutputStream = new ByteArrayOutputStream();
//         byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(excelFile.getInputStream()));
//    }
//
//    @Test
//    void uploadPainFile() {
//        when(painFileServiceMock.processPainFile(any(Mono.class), any(UUID.class))).thenReturn(Mono.just(byteArrayInputStream));
//
//        StepVerifier.create(this.ibanNameCheckBusiness.uploadPainFile(Mono.just(filePartMock), UUID.randomUUID()))
//                .expectSubscription()
//                .consumeNextWith((inputStream) -> log.info("inputStream : {}", inputStream.readAllBytes()))
//                .verifyComplete();
//    }
//
//    @Test
//    void uploadExcelFileAsMono() {
//    }
//
//    @Test
//    void uploadExcelFileAsFlux() {
//    }
//}