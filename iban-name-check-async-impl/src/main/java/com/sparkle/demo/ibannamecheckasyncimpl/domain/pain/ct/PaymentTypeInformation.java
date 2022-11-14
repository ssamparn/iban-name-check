package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentTypeInformation {

    @JsonProperty("InstrPrty")
    private String instructorParty;

    @JsonProperty("SvcLvl")
    private SvcLevel svcLevel;
}
