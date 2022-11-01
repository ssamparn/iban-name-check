package com.sparkle.demo.ibannamecheckasyncimpl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreditTransformationInfo {
    @JsonProperty("GroupHeader")
    private GroupHeader groupHeader;

    @JsonProperty("PaymentInformation")
    private PaymentInformation paymentInformation;
}
