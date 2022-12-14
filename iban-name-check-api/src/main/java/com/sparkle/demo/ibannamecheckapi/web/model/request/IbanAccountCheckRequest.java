package com.sparkle.demo.ibannamecheckapi.web.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IbanAccountCheckRequest {

    private AccountId accountId;

    @JsonProperty("name")
    private String accountHolderName;
}
