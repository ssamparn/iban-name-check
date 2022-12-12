package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.repository.IbanNameRepository;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IbanNameService {

    private final IbanNameResponseFactory ibanNameResponseFactory;
    private final IbanNameRepository ibanNameRepository;

    public Mono<IbanAccountCheckResponse> doAccountCheck(Mono<IbanAccountCheckRequest> requestMono, String xCorrelationId) {

        return requestMono
                .map(ibanNameResponseFactory::toDocument)
                .flatMap(document -> ibanNameRepository.findByAccountNumber(document.getAccountNumber()))
                .map(ibanNameResponseFactory::toModel);

    }
}
