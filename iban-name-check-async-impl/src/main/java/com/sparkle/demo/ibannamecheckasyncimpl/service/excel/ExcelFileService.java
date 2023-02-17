package com.sparkle.demo.ibannamecheckasyncimpl.service.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvReadWriteService;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.ExcelWriteService;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

    private final FileMapper fileMapper;
    private final IbanNameCheckCsvClient ibanNameCheckClient;
    private final CsvReadWriteService csvReadWriteService;
    private final ExcelWriteService excelWriteService;

    public Mono<ByteArrayInputStream> processExcelFileAsMono(Mono<FilePart> filePartMono) {
        return filePartMono
            .map(fileMapper::getFilePartRequestAsInputStream)
            .map(fileMapper::excelToIbanNameModel)
            .flatMap(csvReadWriteService::createCsvRequest)
            .map(InputStreamResource::new)
            .map(ibanNameCheckClient::doPost)
            .map(this::toInputStream)
            .map(this::toBulkResponse)
            .flatMap(excelWriteService::writeToExcel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    private InputStream toInputStream(Flux<DataBuffer> dataBufferFlux) {
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

    private IbanNameCheckResponse toBulkResponse(InputStream inputStream) {
        ObjectMapper mapper = new ObjectMapper();
        IbanNameCheckResponse json = null;
        try {
            json = mapper.readValue(inputStream, IbanNameCheckResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Flux<List<IbanNameModel>> processExcelFileAsFlux(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
