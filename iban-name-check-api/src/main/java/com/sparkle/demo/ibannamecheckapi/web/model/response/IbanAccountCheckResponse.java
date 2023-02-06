package com.sparkle.demo.ibannamecheckapi.web.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IbanAccountCheckResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 507825619827627L;

    @JsonProperty("bulkResponse")
    private List<BulkResponse> batchResponse;
}
