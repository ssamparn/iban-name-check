package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.document.IbanNameDocument;
import com.sparkle.demo.ibannamecheckapi.web.model.request.IbanAccountCheckRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.Account;
import com.sparkle.demo.ibannamecheckapi.web.model.response.AccountHolderType;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResult;
import com.sparkle.demo.ibannamecheckapi.web.model.response.ResultType;
import com.sparkle.demo.ibannamecheckapi.web.model.response.Status;
import org.springframework.stereotype.Service;

@Service
public class IbanNameResponseFactory {

    public IbanNameDocument toDocument(final IbanAccountCheckRequest request) {

        return IbanNameDocument.create(
                null,
                request.getAccountId().getAccountCheckIdentifier(),
                "IBAN",
                request.getAccountHolderName(),
                "D.J.M.",
                "+31604504534");
    }

    public IbanAccountCheckResponse toModel(IbanNameDocument document) {
        IbanAccountCheckResponse response = new IbanAccountCheckResponse();

        IbanAccountCheckResult ibanAccountCheckResult = new IbanAccountCheckResult();
        ibanAccountCheckResult.setResultType(ResultType.MATCHING);
        ibanAccountCheckResult.setSuggestedName(document.getAccountHolderName());

        Account account = new Account();
        account.setIban(document.getAccountNumber());
        account.setAccountHolderType(AccountHolderType.NP);
        account.setStatus(Status.ACTIVE);

        ibanAccountCheckResult.setAccount(account);

        response.setIbanAccountCheckResult(ibanAccountCheckResult);

        return response;
    }
}
