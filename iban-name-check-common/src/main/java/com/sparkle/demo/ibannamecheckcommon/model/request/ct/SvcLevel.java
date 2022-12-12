package com.sparkle.demo.ibannamecheckcommon.model.request.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SvcLevel {

    @JsonProperty("Cd")
    private String cd;
}