package com.sparkle.demo.ibannamecheckasyncimpl.service.excel;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.JsonObjectMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvWriteService;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.ExcelWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

    private final FileMapper fileMapper;
    private final JsonObjectMapper jsonObjectMapper;
    private final IbanNameCheckCsvClient ibanNameCheckClient;
    private final CsvWriteService csvWriteService;
    private final ExcelWriteService excelWriteService;

    public Mono<ByteArrayInputStream> processExcelFileAsMono(Mono<FilePart> filePartMono) {
        return filePartMono
            .map(fileMapper::getFilePartRequestAsInputStream)
            .map(fileMapper::excelToIbanNameModel)
            .flatMap(csvWriteService::createCsvRequest)
            .map(ibanNameCheckClient::doPost)
            .map(fileMapper::getDataBufferAsInputStream)
            .map(jsonObjectMapper::toBulkResponse)
            .flatMap(excelWriteService::writeToExcel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<List<IbanNameModel>> processExcelFileAsFlux(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
