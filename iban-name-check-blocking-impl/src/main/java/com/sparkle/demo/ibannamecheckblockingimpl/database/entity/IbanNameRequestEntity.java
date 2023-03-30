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
@Table(name = "iban_name_request_entity")
public class IbanNameRequestEntity {

    @Id
    private UUID transactionId;

    private UUID correlationId;
    private String counterPartyName;
    private String counterPartyAccount;
}
