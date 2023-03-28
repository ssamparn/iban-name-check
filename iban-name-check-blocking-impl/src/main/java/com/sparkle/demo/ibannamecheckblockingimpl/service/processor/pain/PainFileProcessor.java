package com.sparkle.demo.ibannamecheckblockingimpl.service.processor.pain;

import com.sparkle.demo.ibannamecheckblockingimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckblockingimpl.client.IbanNameCheckJsonClient;
import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameResponseEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameCheckResponseRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.EntityMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.JsonObjectMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskResponse;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskStatusResponse;
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
import java.io.PipedInputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PainFileProcessor {

    private final FileMapper fileMapper;
    private final JsonObjectMapper jsonMapper;
    private final IbanNameCheckJsonClient ibanNameCheckJsonClient;
    private final IbanNameCheckCsvClient ibanNameCheckCsvClient;
    private final ExcelWriteService excelWriteService;
    private final CsvWriteService csvWriteService;
    private final EntityMapper entityMapper;
    private final IbanNameRepository ibanNameRepository;
    private final IbanNameCheckResponseRepository ibanNameCheckResponseRepository;

    public Mono<TaskResponse> processPainFile(Mono<FilePart> filePart, UUID requestId) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(document -> entityMapper.mapToIbanNameEntity(requestId, document))
                .map(entities -> Mono.fromCallable(() -> this.ibanNameRepository.saveAll(entities)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(jsonMapper::toTaskIdRequest)
                .flatMap(csvWriteService::createFirstCsvRequest)
                .flatMap(csvStream -> ibanNameCheckCsvClient.uploadCsvFile(csvStream, requestId));
    }

    public Flux<TaskStatusResponse> checkTaskStatus(UUID taskId) {
        return ibanNameCheckJsonClient.getUpdatedTaskStatus(taskId);
    }

    public Mono<ByteArrayInputStream> processCsvDownload(Mono<FilePart> filePart, UUID taskId) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(document -> entityMapper.mapToIbanNameEntity(taskId, document))
                .map(entities -> Mono.fromCallable(() -> this.ibanNameRepository.saveAll(entities)))
                .flatMap(jsonMapper::mapToIbanNameModel)
                .flatMap(csvWriteService::createCsvRequest)
                .map(requestStream -> ibanNameCheckCsvClient.downloadCsvFile(taskId, requestStream))
                .map(fileMapper::getDataBufferAsInputStream)
                .map(jsonMapper::toCsvDownloadableResource)
                .map(response -> entityMapper.mapToIbanNameCheckResponseEntity(taskId, response))
                .map(entities -> Mono.fromCallable(() -> this.ibanNameCheckResponseRepository.saveAll(entities)))
                .map(jsonMapper::toExcelWritableResource)
                .flatMap(excelWriteService::writeToExcel)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // experimental feature
    public Flux<IbanNameResponseEntity> downloadUpdatedStatus(String correlationId) {
        return Flux.fromIterable(this.ibanNameCheckResponseRepository.getAllByCorrelationId(UUID.fromString(correlationId)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
