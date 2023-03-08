package com.sparkle.demo.ibannamecheckasyncimpl.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IbanNameCheckResponseEntity {

    private String counterPartyAccount;
    private UUID correlationId;
    private String counterPartyName;
    private String finalResult;
    private String info;
    private String suggestedName;
    private String status;
    private String accountHolderType;
}
