package com.sparkle.demo.ibannamecheckcommon.model.surepay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class IbanNameCheckResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 904525619827627L;

    @JsonProperty("bulkResponse")
    private List<BulkResponse> batchResponse;
}
