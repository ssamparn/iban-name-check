package com.sparkle.demo.ibannamecheckblockingimpl.service.operation.pain;

import com.sparkle.demo.ibannamecheckblockingimpl.client.IbanNameCheckCsvClient;
import com.sparkle.demo.ibannamecheckblockingimpl.database.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.EntityMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.mapper.JsonObjectMapper;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.TaskResponse;
import com.sparkle.demo.ibannamecheckblockingimpl.web.service.CsvWriteService;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PainFileService {

    private final EntityMapper entityMapper;
    private final JsonObjectMapper jsonMapper;
    private final IbanNameRepository ibanNameRepository;
    private final CsvWriteService csvWriteService;
    private final IbanNameCheckCsvClient ibanNameCheckCsvClient;

    public Mono<TaskResponse> persistPainFile(final Mono<Document> documentMono, final UUID requestId) {
        return documentMono.map(document -> entityMapper.mapToIbanNameEntity(requestId, document))
                .map(entities -> Mono.fromCallable(() -> this.ibanNameRepository.saveAll(entities)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(jsonMapper::toTaskIdRequest)
                .flatMap(csvWriteService::createFirstCsvRequest)
                .flatMap(csvStream -> ibanNameCheckCsvClient.uploadCsvFile(csvStream, requestId));
    }
}
