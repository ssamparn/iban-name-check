package com.sparkle.demo.ibannamecheckapi.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 67529165391870L;

    private TaskStatus taskStatus;
}
