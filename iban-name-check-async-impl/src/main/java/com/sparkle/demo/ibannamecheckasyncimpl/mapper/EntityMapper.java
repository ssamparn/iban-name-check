package com.sparkle.demo.ibannamecheckasyncimpl.mapper;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalStatus;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.PaymentInformation;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityMapper {

    public List<IbanNameEntity> mapToIbanNameEntity(UUID correlationId, Document rootDocument) {
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

    public List<IbanNameCheckResponseEntity> mapToIbanNameCheckResponseEntity(UUID correlationId, IbanNameCheckResponse surePayResponse) {
        List<IbanNameCheckResponseEntity> ibanNameCheckResponseEntities =  surePayResponse.getBatchResponse().stream()
                .map(bulkResponse -> {
                    IbanNameCheckResponseEntity entity = new IbanNameCheckResponseEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyAccount(bulkResponse.getResult().getAccountResult().getIban());
                    entity.setCounterPartyName(bulkResponse.getResult().getAccountHolderName());

                    entity.setFinalResult(bulkResponse.getResult().getResultType().name());
                    entity.setInfo("small info");
                    entity.setSuggestedName(bulkResponse.getResult().getSuggestedName());
                    entity.setStatus(FinalStatus.ACTIVE.name());
                    entity.setAccountHolderType("ORG");
                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped ibanNameCheckResponseEntities size : {}", ibanNameCheckResponseEntities.size());
        log.info("mapped ibanNameCheckResponseEntities : {}", ibanNameCheckResponseEntities);
        return ibanNameCheckResponseEntities;
    }
}
