package com.sparkle.demo.ibannamecheckapi.web.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IbanAccountCheckResponse {

    @JsonProperty("result")
    private IbanAccountCheckResult ibanAccountCheckResult;
}
