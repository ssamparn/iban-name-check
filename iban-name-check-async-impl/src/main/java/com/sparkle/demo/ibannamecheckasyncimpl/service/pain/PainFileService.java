package com.sparkle.demo.ibannamecheckasyncimpl.service.pain;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckJsonClient;
import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameCheckResponseRepository;
import com.sparkle.demo.ibannamecheckasyncimpl.web.util.FileUtil;
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
    private final FileUtil fileUtil;
    private final IbanNameCheckJsonClient ibanNameCheckClient;
    private final ExcelWriteService excelWriteService;
    private final EntityMapper entityMapper;
    private final IbanNameRepository ibanNameRepository;
    private final IbanNameCheckResponseRepository ibanNameCheckResponseRepository;

    public Mono<ByteArrayInputStream> processPainFile(Mono<FilePart> filePart, UUID correlationId) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(document -> entityMapper.mapToIbanNameEntity(correlationId, document))
                .map(ibanNameRepository::saveAll)
                .flatMap(jsonMapper::toIbanNameCheckRequest)
                .flatMap(ibanNameCheckClient::doPost)
                .map(response -> entityMapper.mapToIbanNameCheckResponseEntity(correlationId, response))
                .map(ibanNameCheckResponseRepository::saveAll)
                .map(jsonMapper::mapToIbanNameCheckData)
                .flatMap(excelWriteService::writeToExcel)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> getMimeType(Mono<FilePart> filePart) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .flatMap(fileUtil::getRealMimeType);
    }

    public Flux<IbanNameCheckResponseEntity> downloadUpdatedStatus(String correlationId) {
        return ibanNameCheckResponseRepository.getAllByCorrelationId(UUID.fromString(correlationId));
    }
}
