package com.sparkle.demo.ibannamecheckapi.web.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class AccountId {

    @JsonProperty("value")
    private String accountIdentifier;

    @JsonProperty("type")
    private AccountType accountType;
}
