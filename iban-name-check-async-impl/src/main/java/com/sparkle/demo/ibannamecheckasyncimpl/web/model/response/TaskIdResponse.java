package com.sparkle.demo.ibannamecheckasyncimpl.web.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskIdResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 507825619827627L;

    @JsonProperty("requestId")
    private UUID requestId;

    @JsonProperty("taskId")
    private UUID taskId;
}
