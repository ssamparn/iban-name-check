package com.sparkle.demo.ibannamecheckasyncimpl.service.pain;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckJsonClient;
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
    private final IbanNameRepository repository;

    public Mono<ByteArrayInputStream> processPainFile(Mono<FilePart> filePart) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(document -> entityMapper.mapToEntity(UUID.fromString("395c60bd-d60f-4a3e-b383-bcc63b0dd1d2"), document))
                .map(repository::saveAll)
                .flatMap(entityMapper::toIbanNameCheckRequest)
                .flatMap(ibanNameCheckClient::doPost)
                .map(jsonMapper::mapToIbanNameCheckData)
                .flatMap(excelWriteService::writeToExcel)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> getMimeType(Mono<FilePart> filePart) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .flatMap(fileUtil::getRealMimeType);
    }
}
