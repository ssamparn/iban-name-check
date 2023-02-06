package com.sparkle.demo.ibannamecheckapi.web.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BulkResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 63025640482768437L;

    @JsonProperty("result")
    private IbanAccountCheckResult result;

}
