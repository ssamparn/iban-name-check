package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInformation {

    @JacksonXmlProperty(localName = "PmtInfId")
    private String paymentInformationId;

    @JacksonXmlProperty(localName = "PmtMtd")
    private String paymentMethod;

    @JacksonXmlProperty(localName = "BtchBookg")
    private Boolean batchBooking;

    @JacksonXmlProperty(localName = "PmtTpInf")
    private PaymentTypeInformation paymentTypeInformation;

    @JacksonXmlProperty(localName = "ReqdExctnDt")
    private String requestedExecutionDate;

    @JacksonXmlProperty(localName = "Dbtr")
    private Debtor debtor;

    @JacksonXmlProperty(localName = "DbtrAcct")
    private DebtorAccount debtorAccount;

    @JacksonXmlProperty(localName = "DbtrAgt")
    private DebtorAgent debtorAgent;

    @JacksonXmlProperty(localName = "CdtTrfTxInf")
    private CreditTransferTransactionInformation creditTransferTransactionInformation;

}
