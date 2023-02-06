package com.sparkle.demo.ibannamecheckcommon.model.ct.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebtorAccount {

    @JacksonXmlProperty(localName = "Id")
    private DebtorAccountId debtorAccountId;

    @JacksonXmlProperty(localName = "Ccy")
    private String debtorAccountCurrency;
}