package com.sparkle.demo.ibannamecheckcommon.model.surepay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class IbanNameCheckRequest {

    @JsonProperty("correlationId")
    private UUID correlationId;

    @JsonProperty("bulkRequest")
    private List<BulkRequest> batchRequest;
}
