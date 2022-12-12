package com.sparkle.demo.ibannamecheckcommon.model.request.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebtorAccountId {

    @JsonProperty("IBAN")
    private String iban;
}
