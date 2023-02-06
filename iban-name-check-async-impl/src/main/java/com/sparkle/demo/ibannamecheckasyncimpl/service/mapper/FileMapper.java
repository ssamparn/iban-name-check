package com.sparkle.demo.ibannamecheckasyncimpl.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.AccountId;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.BulkRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.Document;
import com.sparkle.demo.ibannamecheckcommon.model.ct.request.PaymentInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class FileMapper {

    public Mono<Document> mapToRootDocument(String xmlDocument) {
        XmlMapper xmlMapper = new XmlMapper();
        Document document = null;
        try {
            document = xmlMapper.readValue(xmlDocument, Document.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("root document mapped: {}", document);

        return Mono.just(document);
    }

    public IbanNameCheckRequest mapToSurePayRequest(Document rootDocument) {
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

//        log.info("ROOT DOC" + rootDocument);
//        log.info("ROOT DOC Message Id" + rootDocument.getCustomerCreditTransferInitiation().getGroupHeader().getMessageId());
//        log.info("ROOT DOC Payment Info Id {0}" + rootDocument.getCustomerCreditTransferInitiation().getPaymentInformation().get(0).getPaymentInformationId());

