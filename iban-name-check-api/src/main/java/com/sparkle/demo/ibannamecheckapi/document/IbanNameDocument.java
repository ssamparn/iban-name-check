package com.sparkle.demo.ibannamecheckapi.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor(staticName = "create")
public class IbanNameDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String accountNumber;

    private String accountIdentifierType;

    private String accountHolderName;

    private String accountHolderNameInitials;

    @Indexed(unique = true)
    private String phoneNumber;

}
