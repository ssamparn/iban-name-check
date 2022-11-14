package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Debtor {

    @JsonProperty("Nm")
    private String name;

    @JsonProperty("PstlAdr")
    private PostalAddress postalAddress;
}
