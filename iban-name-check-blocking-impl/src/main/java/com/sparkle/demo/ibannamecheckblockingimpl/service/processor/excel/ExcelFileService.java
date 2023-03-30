package com.sparkle.demo.ibannamecheckblockingimpl.service.processor.excel;

import com.sparkle.demo.ibannamecheckblockingimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.FileRequestRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameCheckResponseRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.EntityMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.JsonObjectMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskResponse;
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
public class ExcelFileService {

    private final FileMapper fileMapper;
    private final JsonObjectMapper jsonMapper;
    private final EntityMapper entityMapper;
    private final IbanNameCheckCsvClient ibanNameCheckCsvClient;
    private final IbanNameRepository ibanNameRepository;
    private final FileRequestRepository fileRequestRepository;
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
            .map(requestStream -> ibanNameCheckCsvClient.downloadCsvFile(requestId, requestStream))
            .map(fileMapper::getDataBufferAsInputStream)
            .map(jsonMapper::toCsvDownloadableResource)
            .map(response -> entityMapper.mapToIbanNameCheckResponseEntity(requestId, response))
            .map(entities -> Mono.fromCallable(() -> this.ibanNameCheckResponseRepository.saveAll(entities)))
            .map(jsonMapper::toExcelWritableResource)
            .flatMap(excelWriteService::writeToExcel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<TaskResponse> processExcelFileRelationship(Mono<FilePart> filePartMono, UUID requestId) {
        return filePartMono
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .map(ibanNameModel -> entityMapper.mapToFileRequestEntity(requestId, ibanNameModel))
                .map(this.fileRequestRepository::save)
                .map(jsonMapper::mapToRelationalIbanNameModel)
                .flatMap(csvWriteService::createCsvRequest)
                .flatMap(csvStream -> ibanNameCheckCsvClient.uploadCsvFile(requestId, csvStream));
    }

    public Mono<TaskResponse> updateTaskId(TaskResponse taskResponse) {
        return Mono.fromCallable(() -> this.fileRequestRepository.findByRequestId(taskResponse.getRequestId().toString()))
            .publishOn(Schedulers.boundedElastic())
                .map(entity -> {
                    entity.setRequestId(taskResponse.getRequestId().toString());
                    entity.setTaskId(taskResponse.getTaskId().toString());
                    return entity;
                })
            .map(fileRequestRepository::save)
            .thenReturn(taskResponse);
    }

    // experimental feature
    public Flux<List<IbanNameModel>> processMultipleExcelFile(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
