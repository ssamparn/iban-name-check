package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Debtor {

    @JacksonXmlProperty(localName = "Nm")
    private String name;

    @JacksonXmlProperty(localName = "PstlAdr")
    private PostalAddress postalAddress;
}
