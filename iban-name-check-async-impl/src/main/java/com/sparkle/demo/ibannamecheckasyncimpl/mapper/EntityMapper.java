package com.sparkle.demo.ibannamecheckasyncimpl.mapper;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameEntity;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.PaymentInformation;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.AccountId;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.BulkRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityMapper {

    public List<IbanNameEntity> mapToEntity(UUID correlationId, Document rootDocument) {
        return rootDocument.getCustomerCreditTransferInitiation().getPaymentInformation()
                .stream()
                .map(PaymentInformation::getCreditTransferTransactionInformation)
                .map(creditTransferTransactionInformation -> {
                    IbanNameEntity entity = new IbanNameEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyName(creditTransferTransactionInformation.getCreditor().getName());
                    entity.setCounterPartyAccount(creditTransferTransactionInformation.getCreditorAccount().getCreditorAccountId().getIban());
                    return entity;
                }).collect(Collectors.toList());
    }

    public Mono<IbanNameCheckRequest> toIbanNameCheckRequest(Flux<IbanNameEntity> ibanNameCheckEntityFlux) {
        return ibanNameCheckEntityFlux.collectList()
                .map(this::mapToSurePayRequest);
    }

    private IbanNameCheckRequest mapToSurePayRequest(List<IbanNameEntity> ibanNameEntities) {
        IbanNameCheckRequest ibanNameCheckRequest = new IbanNameCheckRequest();
        List<BulkRequest> batchRequest = this.mapToBulkRequest(ibanNameEntities);

        ibanNameCheckRequest.setBatchRequest(batchRequest);

        log.info("iban name check request: {}", ibanNameCheckRequest);
        return ibanNameCheckRequest;
    }

    private List<BulkRequest> mapToBulkRequest(List<IbanNameEntity> ibanNameEntities) {
        return ibanNameEntities
                .stream()
                .map(entity -> {
                    BulkRequest bulkRequest = new BulkRequest();
                    bulkRequest.setAccountId(AccountId.create(entity.getCounterPartyAccount(), "IBAN"));
                    bulkRequest.setName(entity.getCounterPartyName());
                    return bulkRequest;
                }).toList();
    }
}
