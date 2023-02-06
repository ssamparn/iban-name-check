package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class PaymentId {

    @JacksonXmlProperty(localName = "InstrId")
    private String instructionId;

    @JacksonXmlProperty(localName = "EndToEndId")
    private String endToEndId;
}
