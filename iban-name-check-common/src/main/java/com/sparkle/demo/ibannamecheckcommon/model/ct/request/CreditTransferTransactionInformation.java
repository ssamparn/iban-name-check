package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditTransferTransactionInformation {

    @JacksonXmlProperty(localName = "PmtId")
    private PaymentId paymentId;

    @JacksonXmlProperty(localName = "Amt")
    private Amount amount;

    @JacksonXmlProperty(localName = "CdtrAgt")
    private CreditorAgent creditorAgent;

    @JacksonXmlProperty(localName = "Cdtr")
    private Creditor creditor;

    @JacksonXmlProperty(localName = "CdtrAcct")
    private CreditorAccount creditorAccount;

    @JacksonXmlProperty(localName = "RmtInf")
    private RemittanceInformation remittanceInformation;
}
