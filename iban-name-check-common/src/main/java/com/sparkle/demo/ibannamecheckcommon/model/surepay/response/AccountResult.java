package com.sparkle.demo.ibannamecheckcommon.model.surepay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AccountResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 7033475619827627L;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("status")
    private AccountStatus accountStatus;

    @JsonProperty("accountHolderType")
    private String accountHolderType;
}
