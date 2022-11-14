package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditTransferTransactionInformation {

    @JsonProperty("PmtId")
    private PaymentId paymentId;

    @JsonProperty("Amt")
    private Amount amount;

    @JsonProperty("CdtrAgt")
    private CreditorAgent creditorAgent;

    @JsonProperty("Cdtr")
    private Creditor creditor;

    @JsonProperty("CdtrAcct")
    private CreditorAccount creditorAccountId;

    @JsonProperty("RmtInf")
    private RemittanceInformation remittanceInformation;
}
