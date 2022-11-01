package com.sparkle.demo.ibannamecheckblockingimpl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class Document {
    @JsonProperty("CustomerCreditTransformationInfo")
    private CustomerCreditTransformationInfo customerCreditTransformationInfo;
}
