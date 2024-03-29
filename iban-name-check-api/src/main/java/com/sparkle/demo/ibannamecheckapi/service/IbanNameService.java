package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckapi.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckapi.web.model.request.AccountId;
import com.sparkle.demo.ibannamecheckapi.web.model.request.BulkJsonRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.TaskIdResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.TaskStatus;
import com.sparkle.demo.ibannamecheckapi.web.model.response.TaskStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.PipedInputStream;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sparkle.demo.ibannamecheckapi.web.model.request.AccountType.IBAN;

@Slf4j
@Service
@RequiredArgsConstructor
public class IbanNameService {

    private final FileMapper fileMapper;
    private final IbanNameResponseFactory ibanNameResponseFactory;
    private final IbanNameRepository ibanNameRepository;
    private final CsvWriteService csvWriteService;

    public Mono<IbanAccountCheckResponse> doJsonPayloadCheck(Mono<IbanAccountCheckRequest> requestMono) {
        return requestMono
            .map(IbanAccountCheckRequest::getBatchRequest)
            .flatMapIterable(bulkRequests -> bulkRequests)
            .map(ibanNameResponseFactory::toDocumentFromJson)
            .flatMap(ibanNameDocument -> ibanNameRepository.findByAccountNumber(ibanNameDocument.getAccountNumber()))
            .collectList()
            .map(ibanNameResponseFactory::toModel)
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ByteArrayInputStream> doCsvPayloadCheck(Mono<FilePart> filePartMono) {
        return filePartMono
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .map(this::processAndGetLinesAsList)
                .map(this::processList)
                .flatMapIterable(bulkRequests -> bulkRequests)
                .map(ibanNameResponseFactory::toDocumentFromJson)
                .flatMap(ibanNameDocument -> ibanNameRepository.findByAccountNumber(ibanNameDocument.getAccountNumber()))
                .collectList()
                .map(ibanNameResponseFactory::toModel)
                .flatMap(csvWriteService::generateCsvResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<String> processAndGetLinesAsList(String string) {
        Supplier<Stream<String>> streamSupplier = string::lines;
        List<String> lines = streamSupplier.get().collect(Collectors.toList());
        lines.remove(0);
        return lines;
    }

    private List<BulkJsonRequest> processList(List<String> accountNames) {
        List<BulkJsonRequest> jsonRequests = accountNames.stream()
                .map(accountName -> accountName.split(","))
                .collect(Collectors.toMap(data -> data[0], data -> data[1]))
                .entrySet()
                .stream()
                .map(entry -> {
                    BulkJsonRequest jsonRequest = new BulkJsonRequest();
                    jsonRequest.setAccountId(AccountId.create(entry.getKey(), IBAN));
                    jsonRequest.setName(entry.getValue());
                    return jsonRequest;
                }).toList();
        return jsonRequests;
    }

    public Mono<TaskIdResponse> createTaskIdResponse(Mono<FilePart> filePartMono, UUID xRequestId) {
        return Mono.just(TaskIdResponse.builder()
                .xRequestId(xRequestId)
                .taskId(UUID.randomUUID())
                .build());
    }

    public List<TaskStatusResponse> createTaskStatusResponse(Long seconds) {
        TaskStatusResponse statusResponse = new TaskStatusResponse();
        statusResponse.setTaskStatus(TaskStatus.randomStatus());

        return List.of(statusResponse);
    }
}
