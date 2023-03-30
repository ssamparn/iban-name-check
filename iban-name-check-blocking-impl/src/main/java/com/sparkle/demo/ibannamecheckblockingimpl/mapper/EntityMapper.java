package com.sparkle.demo.ibannamecheckblockingimpl.mapper;

import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameResponseEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameRequestEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.relationship.FileRequestContentEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.relationship.FileRequestEntity;
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

    public List<IbanNameRequestEntity> mapToIbanNameEntity(UUID correlationId, Document rootDocument) {
        List<IbanNameRequestEntity> ibanNameRequestEntityList = rootDocument.getCustomerCreditTransferInitiation().getPaymentInformation()
                .stream()
                .map(PaymentInformation::getCreditTransferTransactionInformation)
                .map(creditTransferTransactionInformation -> {
                    IbanNameRequestEntity entity = new IbanNameRequestEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyName(creditTransferTransactionInformation.getCreditor().getName());
                    entity.setCounterPartyAccount(creditTransferTransactionInformation.getCreditorAccount().getCreditorAccountId().getIban());
                    entity.setTransactionId(UUID.randomUUID());
                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped iban name entity from pain file list size: {}", ibanNameRequestEntityList.size());
        return ibanNameRequestEntityList;
    }

    public List<IbanNameRequestEntity> mapToIbanNameEntity(UUID correlationId, List<IbanNameModel> ibanNameModels) {
        List<IbanNameRequestEntity> ibanNameRequestEntityList = ibanNameModels.stream()
                .map(model -> {
                    IbanNameRequestEntity entity = new IbanNameRequestEntity();
                    entity.setCorrelationId(correlationId);
                    entity.setCounterPartyName(model.getCounterPartyName());
                    entity.setCounterPartyAccount(model.getCounterPartyAccount());
                    entity.setTransactionId(UUID.randomUUID());
                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped iban name entity from excel file list size: {}", ibanNameRequestEntityList.size());
        return ibanNameRequestEntityList;
    }

    public FileRequestEntity mapToFileRequestEntity(UUID requestId, List<IbanNameModel> ibanNameModels) {
        FileRequestEntity fileRequestEntity = new FileRequestEntity();

        fileRequestEntity.setRequestId(requestId.toString());
        List<FileRequestContentEntity> fileRequestContentEntities = createFileRequestsContentEntity(ibanNameModels);
        fileRequestEntity.setFileRequestContents(fileRequestContentEntities);

        return fileRequestEntity;
    }

    private List<FileRequestContentEntity> createFileRequestsContentEntity(List<IbanNameModel> ibanNameModels) {
        List<FileRequestContentEntity> fileRequestContentEntityList = ibanNameModels.stream()
                .map(model -> {
                    FileRequestContentEntity entity = new FileRequestContentEntity();
                    entity.setCounterPartyName(model.getCounterPartyName());
                    entity.setCounterPartyAccount(model.getCounterPartyAccount());
                    entity.setTransactionId(UUID.randomUUID().toString());
                    return entity;
                }).collect(Collectors.toList());

        log.info("mapped fileRequestContent entity from excel file list size: {}", fileRequestContentEntityList.size());
        return fileRequestContentEntityList;
    }

    public List<IbanNameResponseEntity> mapToIbanNameCheckResponseEntity(UUID correlationId, List<AccountNameCheckData> surePayResponse) {
        List<IbanNameResponseEntity> ibanNameCheckResponseEntities =  surePayResponse.stream()
                .map(response -> {
                    IbanNameResponseEntity entity = new IbanNameResponseEntity();
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

    public List<IbanNameResponseEntity> mapToIbanNameCheckResponseEntity(UUID correlationId, IbanNameCheckResponse surePayResponse) {
        List<IbanNameResponseEntity> ibanNameCheckResponseEntities =  surePayResponse.getBatchResponse().stream()
                .map(bulkResponse -> {
                    IbanNameResponseEntity entity = new IbanNameResponseEntity();
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
