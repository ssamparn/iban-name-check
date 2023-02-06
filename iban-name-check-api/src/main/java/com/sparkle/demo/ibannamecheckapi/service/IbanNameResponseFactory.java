package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.document.IbanNameDocument;
import com.sparkle.demo.ibannamecheckapi.web.model.request.BulkRequest;
import com.sparkle.demo.ibannamecheckapi.web.model.response.Account;
import com.sparkle.demo.ibannamecheckapi.web.model.response.AccountHolderType;
import com.sparkle.demo.ibannamecheckapi.web.model.response.BulkResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResult;
import com.sparkle.demo.ibannamecheckapi.web.model.response.ResultType;
import com.sparkle.demo.ibannamecheckapi.web.model.response.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IbanNameResponseFactory {

    public IbanNameDocument toDocument(final BulkRequest request) {
        return IbanNameDocument.create(
            null,
            request.getAccountId().getAccountIdentifier(),
            request.getAccountId().getAccountType().name(),
            request.getName(),
            "D.J.M.",
            "+31604504534");
    }

    public IbanAccountCheckResponse toModel(List<IbanNameDocument> documents) {
        IbanAccountCheckResponse response = new IbanAccountCheckResponse();
        response.setBatchResponse(this.toBatchResponse(documents));

        return response;
    }

    private List<BulkResponse> toBatchResponse(List<IbanNameDocument> documents) {
        return documents.stream()
            .map(ibanNameDocument -> {
                BulkResponse response = new BulkResponse();
                response.setResult(IbanAccountCheckResult.create(
                    ResultType.MATCHING,
                    ibanNameDocument.getAccountHolderNameInitials(),
                    this.toAccount(ibanNameDocument)
                ));
                return response;
            }).collect(Collectors.toList());
    }

    private Account toAccount(IbanNameDocument ibanNameDocument) {
        return Account.create(
            ibanNameDocument.getAccountNumber(),
            Status.ACTIVE,
            AccountHolderType.ORG
        );
    }
}
