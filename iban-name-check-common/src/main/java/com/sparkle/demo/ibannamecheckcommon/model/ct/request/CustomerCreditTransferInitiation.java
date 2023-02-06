package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreditTransferInitiation {

    @JacksonXmlProperty(localName = "GrpHdr")
    private GroupHeader groupHeader;

    @JacksonXmlProperty(localName = "PmtInf")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PaymentInformation> paymentInformation;
}