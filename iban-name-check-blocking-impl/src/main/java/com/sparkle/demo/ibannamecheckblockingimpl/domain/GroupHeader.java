package com.sparkle.demo.ibannamecheckblockingimpl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupHeader {

    @JsonProperty("MessageId")
    private String messageId;
}
