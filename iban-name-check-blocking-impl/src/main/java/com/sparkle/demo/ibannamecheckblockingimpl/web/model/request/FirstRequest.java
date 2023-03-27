package com.sparkle.demo.ibannamecheckblockingimpl.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class FirstRequest {
    private String counterPartyAccount;
    private String counterPartyName;
    private UUID transactionId;
}
