package com.sparkle.demo.ibannamecheckapi.web.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class IbanAccountCheckResult {

    private ResultType resultType;

    @JsonProperty("nameSuggestion")
    private String suggestedName;

    @JsonProperty("account")
    private Account account;
}
