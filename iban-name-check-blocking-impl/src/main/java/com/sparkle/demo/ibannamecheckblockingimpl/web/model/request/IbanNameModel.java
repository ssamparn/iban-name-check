package com.sparkle.demo.ibannamecheckblockingimpl.web.model.request;

import lombok.Data;

import java.util.UUID;

@Data
public class IbanNameModel {
    private String counterPartyAccount;
    private String counterPartyName;
    private UUID transactionId;
}
