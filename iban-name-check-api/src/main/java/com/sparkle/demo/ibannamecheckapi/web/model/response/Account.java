package com.sparkle.demo.ibannamecheckapi.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Account {

    private String iban;
    private Status status;
    private AccountHolderType accountHolderType;
    private String municipality;
    private SwitchingInformation switchingInformation;
}
