package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditorAccount {

    @JacksonXmlProperty(localName = "Id")
    private CreditorAccountId creditorAccountId;

    @JacksonXmlProperty(localName = "Ccy")
    private String currencyCode;
}
