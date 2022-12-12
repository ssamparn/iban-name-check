package com.sparkle.demo.ibannamecheckapi.web.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountId {

    @JsonProperty("value")
    private String accountCheckIdentifier;

    @JsonProperty("type")
    private AccountType accountType;
}
