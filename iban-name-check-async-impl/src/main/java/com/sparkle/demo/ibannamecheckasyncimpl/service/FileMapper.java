package com.sparkle.demo.ibannamecheckasyncimpl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileMapper {

    public void createPojo(String xmlDocument) throws JsonProcessingException {

        XmlMapper xmlMapper = new XmlMapper();
        Document rootDocument = xmlMapper.readValue(xmlDocument, Document.class);

        log.info("ROOT DOC" + rootDocument);
        log.info("ROOT DOC Message Id" + rootDocument.getCustomerCreditTransferInitiation().getGroupHeader().getMessageId());

        log.info("ROOT DOC Payment Info Id {0}" + rootDocument.getCustomerCreditTransferInitiation().getPaymentInformation().get(0).getPaymentInformationId());
    }
}
