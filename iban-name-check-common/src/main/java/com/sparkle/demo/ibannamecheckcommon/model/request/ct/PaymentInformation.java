package com.sparkle.demo.ibannamecheckcommon.model.request.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInformation {

    @JsonProperty("PmtInfId")
    private String paymentInformationId;

    @JsonProperty("PmtMtd")
    private String paymentMethod;

    @JsonProperty("BtchBookg")
    private Boolean batchBooking;

    @JsonProperty("PmtTpInf")
    private PaymentTypeInformation paymentTypeInformation;

    @JsonProperty("ReqdExctnDt")
    private String requestedExecutionDate;

    @JsonProperty("Dbtr")
    private Debtor debtor;

    @JsonProperty("DbtrAcct")
    private DebtorAccount debtorAccount;

    @JsonProperty("DbtrAgt")
    private DebtorAgent debtorAgent;

    @JsonProperty("CdtTrfTxInf")
    private CreditTransferTransactionInformation creditTransferTransactionInformation;

}
