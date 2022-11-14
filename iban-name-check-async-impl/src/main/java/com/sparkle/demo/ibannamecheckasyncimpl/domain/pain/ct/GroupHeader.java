package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupHeader {

    @JsonProperty("MsgId")
    private String messageId;

    @JsonProperty("CreDtTm")
    private String creditDateTime;

    @JsonProperty("NbOfTxs")
    private Integer numberOfTransactions;

    @JsonProperty("CtrlSum")
    private String controlSum;

    @JsonProperty("InitgPty")
    private InitiatingParty initiatingParty;
}
