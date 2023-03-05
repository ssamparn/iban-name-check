package com.sparkle.demo.ibannamecheckasyncimpl.mapper;

import com.opencsv.bean.CsvToBeanBuilder;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalResult;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalStatus;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.IbanNameCheckData;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JsonObjectMapper {

    public List<IbanNameCheckData> toBulkResponse(InputStream inputStream) {

        List<IbanNameCheckData> ibanNameCheckData = new CsvToBeanBuilder<IbanNameCheckData>(new InputStreamReader(inputStream))
                .withType(IbanNameCheckData.class)
                .build()
                .parse();

        ibanNameCheckData.forEach(data -> log.info("ibanNameCheckData after parsed from CSV : {}", data));

        return ibanNameCheckData;
    }

    public List<IbanNameCheckData> mapToIbanNameCheckData(IbanNameCheckResponse surePayResponse) {
        List<IbanNameCheckData> ibanNameCheckDataList =  surePayResponse.getBatchResponse().stream()
                .map(bulkResponse -> {
                    IbanNameCheckData ibanNameCheckData = new IbanNameCheckData();
                    ibanNameCheckData.setCounterPartyAccountNumber(bulkResponse.getResult().getAccountResult().getIban());
                    ibanNameCheckData.setCounterPartyAccountName(bulkResponse.getResult().getAccountHolderName());

                    ibanNameCheckData.setFinalResult(FinalResult.valueOf(bulkResponse.getResult().getResultType().name()));
                    ibanNameCheckData.setInfo("small info");
                    ibanNameCheckData.setSuggestedName(bulkResponse.getResult().getSuggestedName());
                    ibanNameCheckData.setStatus(FinalStatus.ACTIVE);
                    ibanNameCheckData.setAccountHolderType("ORG");
                    return ibanNameCheckData;
                }).collect(Collectors.toList());

        log.info("mapped ibanNameCheckData size : {}", ibanNameCheckDataList.size());
        log.info("mapped ibanNameCheckData : {}", ibanNameCheckDataList);
        return ibanNameCheckDataList;
    }
}
