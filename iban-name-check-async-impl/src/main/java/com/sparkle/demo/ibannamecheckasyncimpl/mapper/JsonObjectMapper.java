package com.sparkle.demo.ibannamecheckasyncimpl.mapper;

import com.opencsv.bean.CsvToBeanBuilder;
import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalResult;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.FinalStatus;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.IbanNameCheckData;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.AccountId;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.BulkRequest;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.request.IbanNameCheckRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JsonObjectMapper {

    public Mono<IbanNameCheckRequest> toIbanNameCheckRequest(Flux<IbanNameEntity> ibanNameCheckEntityFlux) {
        return ibanNameCheckEntityFlux.collectList()
                .map(this::mapToSurePayRequest);
    }

    private IbanNameCheckRequest mapToSurePayRequest(List<IbanNameEntity> ibanNameEntities) {
        IbanNameCheckRequest ibanNameCheckRequest = new IbanNameCheckRequest();
        List<BulkRequest> batchRequest = this.mapToBulkRequest(ibanNameEntities);

        ibanNameCheckRequest.setBatchRequest(batchRequest);

        log.info("iban name check request: {}", ibanNameCheckRequest);
        return ibanNameCheckRequest;
    }

    private List<BulkRequest> mapToBulkRequest(List<IbanNameEntity> ibanNameEntities) {
        return ibanNameEntities
                .stream()
                .map(entity -> {
                    BulkRequest bulkRequest = new BulkRequest();
                    bulkRequest.setAccountId(AccountId.create(entity.getCounterPartyAccount(), "IBAN"));
                    bulkRequest.setName(entity.getCounterPartyName());
                    return bulkRequest;
                }).toList();
    }

    public List<IbanNameCheckData> toBulkResponse(InputStream inputStream) {

        List<IbanNameCheckData> ibanNameCheckData = new CsvToBeanBuilder<IbanNameCheckData>(new InputStreamReader(inputStream))
                .withType(IbanNameCheckData.class)
                .build()
                .parse();

        ibanNameCheckData.forEach(data -> log.info("ibanNameCheckData after parsed from CSV : {}", data));

        return ibanNameCheckData;
    }

    public List<IbanNameCheckData> mapToIbanNameCheckData(Flux<IbanNameCheckResponseEntity> ibanNameCheckResponseEntityFlux) {
        List<IbanNameCheckResponseEntity> ibanNameCheckResponseEntities = new ArrayList<>();

        ibanNameCheckResponseEntityFlux.collectList()
                .subscribe(ibanNameCheckResponseEntities::addAll);

        log.info("saved ibanNameCheckResponseEntities size: {}", ibanNameCheckResponseEntities.size());

        List<IbanNameCheckData> ibanNameCheckData = ibanNameCheckResponseEntities.stream()
                .map(entity -> {
                    IbanNameCheckData data = new IbanNameCheckData();
                    data.setCounterPartyAccount(entity.getCounterPartyAccount());
                    data.setCounterPartyName(entity.getCounterPartyName());
                    data.setFinalResult(FinalResult.valueOf(entity.getFinalResult()));
                    data.setInfo(entity.getInfo());
                    data.setSuggestedName(entity.getSuggestedName());
                    data.setStatus(FinalStatus.valueOf(entity.getStatus()));
                    data.setAccountHolderType(entity.getAccountHolderType());

                    return data;
                }).collect(Collectors.toList());

        log.info("ibanNameCheckData size: {}", ibanNameCheckData.size());

        return ibanNameCheckData;
    }
}
