package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "Document")
public class Document {

    @JsonProperty("CstmrCdtTrfInitn")
    private CustomerCreditTransferInitiation customerCreditTransferInitiation;

}
