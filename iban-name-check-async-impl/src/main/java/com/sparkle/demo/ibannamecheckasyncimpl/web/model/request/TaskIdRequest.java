package com.sparkle.demo.ibannamecheckasyncimpl.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class TaskIdRequest {
    private List<FirstRequest> firstRequestList;
}
