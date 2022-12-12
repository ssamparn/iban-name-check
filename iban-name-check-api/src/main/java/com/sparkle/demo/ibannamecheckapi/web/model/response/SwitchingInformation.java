package com.sparkle.demo.ibannamecheckapi.web.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SwitchingInformation {
    private SwitchServiceForAccount switchServiceForAccount;
    private String switchedToIban;
}
