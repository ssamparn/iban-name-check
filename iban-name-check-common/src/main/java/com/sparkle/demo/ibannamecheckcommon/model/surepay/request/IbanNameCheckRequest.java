package com.sparkle.demo.ibannamecheckcommon.model.surepay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class IbanNameCheckRequest {

    @JsonProperty("bulkRequest")
    private List<BulkRequest> batchRequest;
}
