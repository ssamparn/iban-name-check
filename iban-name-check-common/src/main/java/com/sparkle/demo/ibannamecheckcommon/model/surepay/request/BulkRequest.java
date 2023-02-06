package com.sparkle.demo.ibannamecheckcommon.model.surepay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class BulkRequest {

    @JsonProperty("accountId")
    private AccountId accountId;

    @JsonProperty("name")
    private String name;
}
