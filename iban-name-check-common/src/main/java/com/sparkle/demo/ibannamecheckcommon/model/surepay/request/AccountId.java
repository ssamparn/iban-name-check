package com.sparkle.demo.ibannamecheckcommon.model.surepay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class AccountId {

    private String value;

    private String type;
}
