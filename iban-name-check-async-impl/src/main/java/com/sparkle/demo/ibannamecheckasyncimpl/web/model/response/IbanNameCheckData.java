package com.sparkle.demo.ibannamecheckasyncimpl.web.model.response;

import lombok.Data;

@Data
public class IbanNameCheckData {
    private String counterPartyAccountNumber;
    private String counterPartyAccountName;

    private FinalResult finalResult;
    private String info;
    private String suggestedName;

    private FinalStatus status;
    private String accountHolderType;
}
