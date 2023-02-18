package com.sparkle.demo.ibannamecheckcommon.model.surepay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class IbanNameCheckResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 893025640482768437L;

    private ResultType resultType;

    @JsonProperty("name")
    private String accountHolderName;

    @JsonProperty("nameSuggestion")
    private String suggestedName;

    @JsonProperty("account")
    private AccountResult accountResult;
}
