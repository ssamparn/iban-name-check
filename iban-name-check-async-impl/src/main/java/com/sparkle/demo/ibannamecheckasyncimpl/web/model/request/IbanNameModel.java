package com.sparkle.demo.ibannamecheckasyncimpl.web.model.request;

import lombok.Data;

@Data
public class IbanNameModel {
    private String counterPartyAccount;
    private String counterPartyName;
}
