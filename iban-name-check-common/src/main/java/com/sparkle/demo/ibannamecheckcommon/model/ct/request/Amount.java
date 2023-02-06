package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Amount {

    @JsonProperty("InstdAmt")
    private String instructedAmount;
}
