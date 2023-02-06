package com.sparkle.demo.ibannamecheckasyncimpl.web.model;

import lombok.Data;

@Data
public class IbanNameCheckData {
    private String account;
    private String status;
    private boolean matched;
}
