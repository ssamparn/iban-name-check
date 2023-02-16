package com.sparkle.demo.ibannamecheckasyncimpl.service.excel;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.IbanNameCheckData;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvReadWriteService;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

    private final FileMapper fileMapper;
    private final IbanNameCheckCsvClient ibanNameCheckClient;
    private final CsvReadWriteService csvReadWriteService;

    public Mono<ByteArrayInputStream> processExcelFileAsMono(Mono<FilePart> filePartMono) {
        return filePartMono
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .flatMap(csvReadWriteService::createCsvRequest)
                .map(InputStreamResource::new)
                .flatMap(ibanNameCheckClient::doPost)
                .map(this::toIbanNameCheckData)
                .flatMap(csvReadWriteService::generateCsvResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<IbanNameCheckData> toIbanNameCheckData(IbanNameCheckResponse ibanNameCheckResponse) {
        return Collections.emptyList();
    }

    public Flux<List<IbanNameModel>> processExcelFileAsFlux(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
