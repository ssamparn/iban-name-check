package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class IbanNameService {

    private final IbanNameResponseFactory ibanNameResponseFactory;
    private final IbanNameRepository ibanNameRepository;

    public Mono<IbanAccountCheckResponse> doAccountCheck(Mono<IbanAccountCheckRequest> requestMono) {
        return requestMono
            .map(IbanAccountCheckRequest::getBatchRequest)
            .flatMapIterable(bulkRequests -> bulkRequests)
            .map(ibanNameResponseFactory::toDocument)
            .flatMap(ibanNameDocument -> ibanNameRepository.findByAccountNumber(ibanNameDocument.getAccountNumber()))
            .collectList()
            .map(ibanNameResponseFactory::toModel)
            .subscribeOn(Schedulers.boundedElastic());
    }
}
