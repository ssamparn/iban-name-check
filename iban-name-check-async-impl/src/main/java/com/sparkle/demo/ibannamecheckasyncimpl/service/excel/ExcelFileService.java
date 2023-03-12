package com.sparkle.demo.ibannamecheckasyncimpl.service.excel;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameCheckResponseRepository;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.EntityMapper;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

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
            .map(inputStream -> fileMapper.excelToIbanNameModel(requestId, inputStream))
            .map(ibanNameModel -> entityMapper.mapToIbanNameEntity(requestId, ibanNameModel))
            .map(ibanNameRepository::saveAll)
            .flatMap(jsonMapper::mapToIbanNameModel)
            .flatMap(csvWriteService::createCsvRequest)
            .map(requestStream -> ibanNameCheckClient.downloadCsvFile(requestId, requestStream))
            .map(fileMapper::getDataBufferAsInputStream)
            .map(jsonMapper::toCsvDownloadableResource)
            .map(response -> entityMapper.mapToIbanNameCheckResponseEntity(requestId, response))
            .map(ibanNameCheckResponseRepository::saveAll)
            .map(jsonMapper::toExcelWritableResource)
            .flatMap(excelWriteService::writeToExcel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    // experimental feature
    public Flux<List<IbanNameModel>> processMultipleExcelFile(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> fileMapper.excelToIbanNameModel(UUID.randomUUID(), inputStream))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
