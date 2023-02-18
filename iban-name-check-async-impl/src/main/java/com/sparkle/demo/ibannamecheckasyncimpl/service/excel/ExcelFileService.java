package com.sparkle.demo.ibannamecheckasyncimpl.service.excel;

import com.opencsv.bean.CsvToBeanBuilder;
import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.IbanNameCheckData;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvWriteService;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.ExcelWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

    private final FileMapper fileMapper;
    private final IbanNameCheckCsvClient ibanNameCheckClient;
    private final CsvWriteService csvWriteService;
    private final ExcelWriteService excelWriteService;

    public Mono<ByteArrayInputStream> processExcelFileAsMono(Mono<FilePart> filePartMono) {
        return filePartMono
            .map(fileMapper::getFilePartRequestAsInputStream)
            .map(fileMapper::excelToIbanNameModel)
            .flatMap(csvWriteService::createCsvRequest)
            .map(ibanNameCheckClient::doPost)
            .map(this::toInputStream)
            .map(this::toBulkResponse)
            .flatMap(excelWriteService::writeToExcel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<List<IbanNameModel>> processExcelFileAsFlux(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
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

    private List<IbanNameCheckData> toBulkResponse(InputStream inputStream) {

        List<IbanNameCheckData> ibanNameCheckData = new CsvToBeanBuilder<IbanNameCheckData>(new InputStreamReader(inputStream))
                .withType(IbanNameCheckData.class)
                .build()
                .parse();

        ibanNameCheckData.forEach(data -> log.info("ibanNameCheckData after parsed from CSV : {}", data));

        return ibanNameCheckData;
    }
}
