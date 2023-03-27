package com.sparkle.demo.ibannamecheckblockingimpl.mapper;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.AccountNameCheckData;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.FinalStatus;
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
        List<IbanNameEntity> ibanNameEntityList = rootDocument.getCustomerCreditTransferInitiation().getPaymentInformation()
                .stream()
                .map(PaymentInformation::getCreditTransferTransactionInformation)
                .map(creditTransferTransactionInformation -> {
                    IbanNameEntity entity = new IbanNameEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyName(creditTransferTransactionInformation.getCreditor().getName());
                    entity.setCounterPartyAccount(creditTransferTransactionInformation.getCreditorAccount().getCreditorAccountId().getIban());
                    entity.setTransactionId(UUID.randomUUID());
                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped iban name entity from pain file list size: {}", ibanNameEntityList.size());
        return ibanNameEntityList;
    }

    public List<IbanNameEntity> mapToIbanNameEntity(UUID correlationId, List<IbanNameModel> ibanNameModels) {
        return ibanNameModels.stream()
                .map(model -> {
                    IbanNameEntity entity = new IbanNameEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyName(model.getCounterPartyName());
                    entity.setCounterPartyAccount(model.getCounterPartyAccount());
                    return entity;
                }).collect(Collectors.toList());
    }

    public List<IbanNameCheckResponseEntity> mapToIbanNameCheckResponseEntity(UUID correlationId, List<AccountNameCheckData> surePayResponse) {
        List<IbanNameCheckResponseEntity> ibanNameCheckResponseEntities =  surePayResponse.stream()
                .map(response -> {
                    IbanNameCheckResponseEntity entity = new IbanNameCheckResponseEntity();
                    entity.setCounterPartyAccount(response.getCounterPartyAccount());
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyName(response.getCounterPartyName());
                    entity.setTransactionId(UUID.randomUUID());
                    entity.setMatchingResult(response.getFinalResult().name());
                    entity.setAccountStatus(FinalStatus.ACTIVE.name());
                    entity.setAccountHolderType("ORG");
                    entity.setSwitchingServiceStatus("ACTIVE");
                    entity.setSwitchedToIban(response.getSwitchedToIban());
                    entity.setMessage(response.getMessage());

                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped ibanNameCheckResponseEntities size : {}", ibanNameCheckResponseEntities.size());
        log.info("mapped ibanNameCheckResponseEntities : {}", ibanNameCheckResponseEntities);
        return ibanNameCheckResponseEntities;
    }

    public List<IbanNameCheckResponseEntity> mapToIbanNameCheckResponseEntity(UUID correlationId, IbanNameCheckResponse surePayResponse) {
        List<IbanNameCheckResponseEntity> ibanNameCheckResponseEntities =  surePayResponse.getBatchResponse().stream()
                .map(bulkResponse -> {
                    IbanNameCheckResponseEntity entity = new IbanNameCheckResponseEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyAccount(bulkResponse.getResult().getAccountResult().getIban());
                    entity.setCounterPartyName(bulkResponse.getResult().getAccountHolderName());
                    entity.setMatchingResult(bulkResponse.getResult().getResultType().name());
                    entity.setAccountStatus(FinalStatus.ACTIVE.name());
                    entity.setAccountHolderType("ORG");
                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped ibanNameCheckResponseEntities size : {}", ibanNameCheckResponseEntities.size());
        log.info("mapped ibanNameCheckResponseEntities : {}", ibanNameCheckResponseEntities);
        return ibanNameCheckResponseEntities;
    }
}
