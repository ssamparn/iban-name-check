package com.sparkle.demo.ibannamecheckasyncimpl.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileMapper {

    public Mono<Document> mapToRootDocument(String xmlDocument) {
        XmlMapper xmlMapper = new XmlMapper();
        Document document = null;
        try {
            document = xmlMapper.readValue(xmlDocument, Document.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("root document mapped: {}", document);

        return Mono.just(document);
    }

    public InputStream getFilePartRequestAsInputStream(FilePart filePart) {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 30 * 30);
        try {
            pipedInputStream.connect(pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Flux<DataBuffer> body = filePart.content();
        DataBufferUtils.write(body, pipedOutputStream)
                .log("Writing to output buffer")
                .subscribe();

        return pipedInputStream;
    }

    public InputStream getDataBufferAsInputStream(Flux<DataBuffer> dataBufferFlux) {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 30 * 30);
        try {
            pipedInputStream.connect(pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataBufferUtils.write(dataBufferFlux, pipedOutputStream)
                .log("Writing to output buffer")
                .subscribeOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException ignored) {

                    }
                })
                .subscribe(DataBufferUtils.releaseConsumer());
        return pipedInputStream;
    }

    public Mono<String> readContentFromPipedInputStream(PipedInputStream stream) {
        StringBuffer contentStringBuffer = new StringBuffer();
        try {
            Thread pipeReader = new Thread(() -> {
                try {
                    contentStringBuffer.append(readContent(stream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pipeReader.start();
            pipeReader.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Mono.just(String.valueOf(contentStringBuffer));
    }

    private String readContent(InputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        byte[] tmp = new byte[stream.available()];
        int byteCount = stream.read(tmp, 0, tmp.length);
        log.info(String.format("read %d bytes from the stream\n", byteCount));
        contentStringBuffer.append(new String(tmp));
        return String.valueOf(contentStringBuffer);
    }

    public List<IbanNameModel> excelToIbanNameModel(UUID requestId, InputStream inputStream) {
        List<IbanNameModel> ibanNameModelList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet worksheet = workbook.getSheetAt(0);
            for(int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                IbanNameModel ibanNameModel = new IbanNameModel();
                XSSFRow row = worksheet.getRow(i);

                ibanNameModel.setCounterPartyAccount(row.getCell(0).getStringCellValue().trim());
                ibanNameModel.setCounterPartyName(row.getCell(1).getStringCellValue().trim());
                ibanNameModel.setTransactionId(UUID.randomUUID());
                ibanNameModelList.add(ibanNameModel);
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ibanNameModelList;
    }
}
