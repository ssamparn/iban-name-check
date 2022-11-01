package com.sparkle.demo.ibannamecheckasyncimpl.domain;

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
