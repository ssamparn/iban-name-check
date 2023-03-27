package com.sparkle.demo.ibannamecheckblockingimpl.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "iban_name_response_entity")
public class IbanNameCheckResponseEntity {

    private String counterPartyAccount;
    private UUID correlationId;
    private String counterPartyName;

    @Id
    private UUID transactionId;

    private String matchingResult;
    private String accountStatus;
    private String accountHolderType;
    private String switchingServiceStatus;
    private String switchedToIban;
    private String message;
}
