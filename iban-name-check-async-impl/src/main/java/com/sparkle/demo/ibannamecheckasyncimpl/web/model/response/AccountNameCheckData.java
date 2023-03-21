package com.sparkle.demo.ibannamecheckasyncimpl.web.model.response;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class AccountNameCheckData {

    @CsvBindByName(column = "IBAN")
    private String counterPartyAccount;

    @CsvBindByName(column = "NAME")
    private String counterPartyName;

    @CsvBindByName(column = "TRANSACTION_ID")
    private String transactionId;

    @CsvBindByName(column = "MATCHING_RESULT")
    private FinalResult finalResult;

    @CsvBindByName(column = "ACCOUNT_STATUS")
    private FinalStatus status;

    @CsvBindByName(column = "ACCOUNT_HOLDER_TYPE")
    private String accountHolderType;

    @CsvBindByName(column = "SWITCHING_SERVICE_STATUS")
    private String switchingServiceStatus;

    @CsvBindByName(column = "SWITCHED_TO_IBAN")
    private String switchedToIban;

    @CsvBindByName(column = "MESSAGE")
    private String message;
}
