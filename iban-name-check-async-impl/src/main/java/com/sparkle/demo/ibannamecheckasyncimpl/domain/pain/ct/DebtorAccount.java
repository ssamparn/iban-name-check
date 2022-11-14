package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebtorAccount {

    @JsonProperty("Id")
    private DebtorAccountId debtorAccountId;

    @JsonProperty("Ccy")
    private String debtorAccountCurrency;
}