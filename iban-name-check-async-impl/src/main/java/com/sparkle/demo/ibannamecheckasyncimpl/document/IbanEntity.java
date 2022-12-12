package com.sparkle.demo.ibannamecheckasyncimpl.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class IbanEntity {

    @Id
    private String id;

    private String iban;
}
