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
public class IbanNameEntity {

    private UUID correlationId;
    private String counterPartyName;
    private String counterPartyAccount;
}
