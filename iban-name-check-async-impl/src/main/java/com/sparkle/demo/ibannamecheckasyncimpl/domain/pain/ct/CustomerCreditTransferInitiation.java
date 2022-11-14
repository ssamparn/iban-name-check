package com.sparkle.demo.ibannamecheckasyncimpl.domain.pain.ct;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreditTransferInitiation {

    @JsonProperty("GrpHdr")
    private GroupHeader groupHeader;

    @JsonProperty("PmtInf")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PaymentInformation> paymentInformation = new ArrayList<>();
}