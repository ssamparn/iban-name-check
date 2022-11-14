package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostalAddress {

    @JsonProperty("Ctry")
    private String country;

    @JsonProperty("AdrLine")
    private String addressLine;
}
