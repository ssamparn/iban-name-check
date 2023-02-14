package com.sparkle.demo.ibannamecheckasyncimpl.service.handler.pain;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckClient;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.PaymentInformation;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.AccountId;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.BulkRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.ResultType;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import com.sparkle.demo.ibannamecheckasyncimpl.service.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.IbanNameCheckData;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvWriterService;
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
    private final IbanNameCheckClient ibanNameCheckClient;
    private final CsvWriterService csvWriterService;

    public Mono<ByteArrayInputStream> uploadFile(Mono<FilePart> filePart) {
        return filePart
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream) // Refactor this line
                .flatMap(fileMapper::readContentFromPipedInputStream)// Refactor this line
                .flatMap(fileMapper::mapToRootDocument)
                .map(this::mapToSurePayRequest)
                .flatMap(ibanNameCheckClient::doPost)
                .map(this::mapToIbanNameCheckData)
                .flatMap(csvWriterService::generateCsv)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<IbanNameCheckData> mapToIbanNameCheckData(IbanNameCheckResponse surePayResponse) {
        List<IbanNameCheckData> ibanNameCheckDataList =  surePayResponse.getBatchResponse().stream()
            .map(bulkResponse -> {
                IbanNameCheckData ibanNameCheckData = new IbanNameCheckData();
                ibanNameCheckData.setAccount(bulkResponse.getResult().getAccountResult().getIban());
                ibanNameCheckData.setStatus(bulkResponse.getResult().getAccountResult().getAccountStatus().name());
                ibanNameCheckData.setMatched(bulkResponse.getResult().getResultType() == ResultType.MATCHING);
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
