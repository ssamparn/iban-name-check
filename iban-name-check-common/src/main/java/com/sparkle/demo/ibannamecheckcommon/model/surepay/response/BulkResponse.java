package com.sparkle.demo.ibannamecheckcommon.model.surepay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BulkResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 982595482796833L;

    @JsonProperty("result")
    private IbanNameCheckResult result;

}
