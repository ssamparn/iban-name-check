package com.sparkle.demo.ibannamecheckasyncimpl.web.model.response;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class IbanNameCheckData {

    @CsvBindByName(column = "Iban")
    private String counterPartyAccountNumber;

    @CsvBindByName(column = "Naam")
    private String counterPartyAccountName;

    @CsvBindByName(column = "Resultaat")
    private FinalResult finalResult;

    @CsvBindByName(column = "Info")
    private String info;

    @CsvBindByName(column = "Naam Suggestie")
    private String suggestedName;

    @CsvBindByName(column = "Status")
    private FinalStatus status;

    @CsvBindByName(column = "AccountHolderType")
    private String accountHolderType;
}
