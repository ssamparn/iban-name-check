package com.sparkle.demo.ibannamecheckasyncimpl.service.pain;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckJsonClient;
import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameCheckResponseRepository;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.TaskIdResponse;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.TaskStatusResponse;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvWriteService;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.EntityMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.JsonObjectMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.ExcelWriteService;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.FileMapper;
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
public class PainFileService {

    private final FileMapper fileMapper;
    private final JsonObjectMapper jsonMapper;
    private final IbanNameCheckJsonClient ibanNameCheckJsonClient;
    private final IbanNameCheckCsvClient ibanNameCheckCsvClient;
    private final ExcelWriteService excelWriteService;
    private final CsvWriteService csvWriteService;
    private final EntityMapper entityMapper;
    private final IbanNameRepository ibanNameRepository;
    private final IbanNameCheckResponseRepository ibanNameCheckResponseRepository;

    public Mono<TaskIdResponse> processPainFile(Mono<FilePart> filePart, UUID requestId) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(document -> entityMapper.mapToIbanNameEntity(requestId, document))
                .map(ibanNameRepository::saveAll)
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
                .map(ibanNameRepository::saveAll)
                .flatMap(jsonMapper::mapToIbanNameModel)
                .flatMap(csvWriteService::createCsvRequest)
                .map(requestStream -> ibanNameCheckCsvClient.downloadCsvFile(taskId, requestStream))
                .map(fileMapper::getDataBufferAsInputStream)
                .map(jsonMapper::toCsvDownloadableResource)
                .map(response -> entityMapper.mapToIbanNameCheckResponseEntity(taskId, response))
                .map(ibanNameCheckResponseRepository::saveAll)
                .map(jsonMapper::toExcelWritableResource)
                .flatMap(excelWriteService::writeToExcel)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // experimental feature
    public Flux<IbanNameCheckResponseEntity> downloadUpdatedStatus(String correlationId) {
        return ibanNameCheckResponseRepository.getAllByCorrelationId(UUID.fromString(correlationId));
    }
}
