package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiatingParty {

    @JsonProperty("Nm")
    private String name;
}
