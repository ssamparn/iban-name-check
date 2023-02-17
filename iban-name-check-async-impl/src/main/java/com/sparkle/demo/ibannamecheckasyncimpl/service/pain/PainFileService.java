package com.sparkle.demo.ibannamecheckasyncimpl.service.pain;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckJsonClient;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalResult;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalStatus;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.ExcelWriteService;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.PaymentInformation;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.AccountId;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.BulkRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import com.sparkle.demo.ibannamecheckasyncimpl.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.IbanNameCheckData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.PipedInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PainFileService {

    private final FileMapper fileMapper;
    private final IbanNameCheckJsonClient ibanNameCheckClient;
    private final ExcelWriteService excelWriteService;

    public Mono<ByteArrayInputStream> processPainFile(Mono<FilePart> filePart) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(fileMapper::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(this::mapToSurePayRequest)
                .flatMap(ibanNameCheckClient::doPost)
                .map(this::mapToIbanNameCheckData)
                .flatMap(excelWriteService::writeToExcel)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<IbanNameCheckData> mapToIbanNameCheckData(IbanNameCheckResponse surePayResponse) {
        List<IbanNameCheckData> ibanNameCheckDataList =  surePayResponse.getBatchResponse().stream()
            .map(bulkResponse -> {
                IbanNameCheckData ibanNameCheckData = new IbanNameCheckData();
                ibanNameCheckData.setCounterPartyAccountNumber(bulkResponse.getResult().getAccountResult().getIban());
                ibanNameCheckData.setCounterPartyAccountName(bulkResponse.getResult().getAccountResult().getAccountHolderType());

                ibanNameCheckData.setFinalResult(FinalResult.valueOf(bulkResponse.getResult().getResultType().name()));
                ibanNameCheckData.setInfo("small info");
                ibanNameCheckData.setSuggestedName(bulkResponse.getResult().getSuggestedName());
                ibanNameCheckData.setStatus(FinalStatus.ACTIVE);
                ibanNameCheckData.setAccountHolderType("ORG");
                return ibanNameCheckData;
            }).collect(Collectors.toList());

        log.info("mapped ibanNameCheckData size : {}", ibanNameCheckDataList.size());
        log.info("mapped ibanNameCheckData : {}", ibanNameCheckDataList);
        return ibanNameCheckDataList;
    }

    private IbanNameCheckRequest mapToSurePayRequest(Document rootDocument) {
        IbanNameCheckRequest ibanNameCheckRequest = new IbanNameCheckRequest();
        List<BulkRequest> batchRequest = this.mapToBulkRequest(rootDocument);

        ibanNameCheckRequest.setBatchRequest(batchRequest);

        log.info("iban name check request: {}", ibanNameCheckRequest);
        return ibanNameCheckRequest;
    }

    private List<BulkRequest> mapToBulkRequest(Document rootDocument) {
        return rootDocument.getCustomerCreditTransferInitiation().getPaymentInformation()
                .stream()
                .map(PaymentInformation::getCreditTransferTransactionInformation)
                .map(creditTransferTransactionInformation -> {
                    BulkRequest bulkRequest = new BulkRequest();
                    bulkRequest.setAccountId(AccountId.create(creditTransferTransactionInformation.getCreditorAccount().getCreditorAccountId().getIban(), "IBAN"));
                    bulkRequest.setName(creditTransferTransactionInformation.getCreditor().getName());
                    return bulkRequest;
                }).toList();
    }
}
