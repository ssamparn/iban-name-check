package com.sparkle.demo.ibannamecheckcommon.model.request.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentId {

    @JsonProperty("InstrId")
    private String instructionId;

    @JsonProperty("EndToEndId")
    private String endToEndId;
}
