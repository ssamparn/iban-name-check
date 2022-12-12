package com.sparkle.demo.ibannamecheckcommon.model.request.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditorAccount {

    @JsonProperty("Id")
    private CreditorAccountId creditorAccountId;

    @JsonProperty("Ccy")
    private String currencyCode;
}
