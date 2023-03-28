package com.sparkle.demo.ibannamecheckblockingimpl.service.processor.excel;

import com.sparkle.demo.ibannamecheckblockingimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameCheckResponseRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.EntityMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.JsonObjectMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckblockingimpl.web.service.CsvWriteService;
import com.sparkle.demo.ibannamecheckblockingimpl.web.service.ExcelWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileProcessor {

    private final FileMapper fileMapper;
    private final JsonObjectMapper jsonMapper;
    private final EntityMapper entityMapper;
    private final IbanNameCheckCsvClient ibanNameCheckClient;
    private final IbanNameRepository ibanNameRepository;
    private final IbanNameCheckResponseRepository ibanNameCheckResponseRepository;
    private final CsvWriteService csvWriteService;
    private final ExcelWriteService excelWriteService;

    public Mono<ByteArrayInputStream> processExcelFile(Mono<FilePart> filePartMono, UUID requestId) {
        return filePartMono
            .map(fileMapper::getFilePartRequestAsInputStream)
            .map(fileMapper::excelToIbanNameModel)
            .map(ibanNameModel -> entityMapper.mapToIbanNameEntity(requestId, ibanNameModel))
            .map(entities -> Mono.fromCallable(() -> this.ibanNameRepository.saveAll(entities)))
            .flatMap(jsonMapper::mapToIbanNameModel)
            .flatMap(csvWriteService::createCsvRequest)
            .map(requestStream -> ibanNameCheckClient.downloadCsvFile(requestId, requestStream))
            .map(fileMapper::getDataBufferAsInputStream)
            .map(jsonMapper::toCsvDownloadableResource)
            .map(response -> entityMapper.mapToIbanNameCheckResponseEntity(requestId, response))
            .map(entities -> Mono.fromCallable(() -> this.ibanNameCheckResponseRepository.saveAll(entities)))
            .map(jsonMapper::toExcelWritableResource)
            .flatMap(excelWriteService::writeToExcel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    // experimental feature
    public Flux<List<IbanNameModel>> processMultipleExcelFile(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
