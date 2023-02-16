package com.sparkle.demo.ibannamecheckapi.web.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class IbanAccountCheckRequest {

    @JsonProperty("bulkRequest")
    private List<BulkJsonRequest> batchRequest;
}
