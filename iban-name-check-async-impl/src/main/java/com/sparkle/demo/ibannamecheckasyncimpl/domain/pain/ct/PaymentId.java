package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentId {

    @JsonProperty("InstrId")
    private String instructionId;

    @JsonProperty("EndToEndId")
    private String endToEndId;
}
