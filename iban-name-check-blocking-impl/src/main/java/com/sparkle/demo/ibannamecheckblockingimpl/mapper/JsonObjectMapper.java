package com.sparkle.demo.ibannamecheckblockingimpl.mapper;

import com.opencsv.bean.CsvToBeanBuilder;
import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameCheckResponseEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.database.entity.IbanNameEntity;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.FirstRequest;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.IbanNameModel;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.request.TaskIdRequest;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.AccountNameCheckData;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.FinalResult;
import com.sparkle.demo.ibannamecheckblockingimpl.web.model.response.FinalStatus;
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

    public Mono<TaskIdRequest> toTaskIdRequest(Mono<List<IbanNameEntity>> listMono) {
        return listMono.map(this::mapToTaskIdRequest);
    }

    private TaskIdRequest mapToTaskIdRequest(List<IbanNameEntity> ibanNameEntities) {
        TaskIdRequest taskIdRequest = new TaskIdRequest();
        List<FirstRequest> batchRequest = this.mapToFirstRequest(ibanNameEntities);
        taskIdRequest.setFirstRequestList(batchRequest);

        return taskIdRequest;
    }

    private List<FirstRequest> mapToFirstRequest(List<IbanNameEntity> ibanNameEntities) {
        return ibanNameEntities
                .stream()
                .map(entity -> {
                    FirstRequest request = new FirstRequest();
                    request.setCounterPartyAccount(entity.getCounterPartyAccount());
                    request.setCounterPartyName(entity.getCounterPartyName());
                    request.setTransactionId(entity.getTransactionId());

                    return request;
                }).collect(Collectors.toList());
    }

    public Mono<List<IbanNameModel>> mapToIbanNameModel(Mono<List<IbanNameEntity>> listMono) {
        return listMono.map(this::mapToIbanNameModelList);
    }

    private List<IbanNameModel> mapToIbanNameModelList(List<IbanNameEntity> ibanNameEntities) {
        return ibanNameEntities.stream()
                .map(entity -> {
                    IbanNameModel model = new IbanNameModel();
                    model.setTransactionId(entity.getTransactionId());
                    model.setCounterPartyAccount(entity.getCounterPartyAccount());
                    model.setCounterPartyName(entity.getCounterPartyName());
                    return model;
                }).collect(Collectors.toList());
    }

    public Mono<IbanNameCheckRequest> toSurePayJsonRequest(Flux<IbanNameEntity> ibanNameCheckEntityFlux) {
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
                }).collect(Collectors.toList());
    }

    public List<AccountNameCheckData> toCsvDownloadableResource(InputStream inputStream) {

        List<AccountNameCheckData> accountNameCheckData = new CsvToBeanBuilder<AccountNameCheckData>(new InputStreamReader(inputStream))
                .withType(AccountNameCheckData.class)
                .build()
                .parse();

        accountNameCheckData.forEach(data -> log.info("sure pay response after parsed from CSV : {}", data));

        return accountNameCheckData;
    }

    public List<AccountNameCheckData> toExcelWritableResource(Mono<List<IbanNameCheckResponseEntity>> ibanNameCheckResponseEntityMonoList) {
        List<IbanNameCheckResponseEntity> ibanNameCheckResponseEntities = new ArrayList<>();

        ibanNameCheckResponseEntities = ibanNameCheckResponseEntityMonoList.block();

        log.info("saved ibanNameCheckResponseEntities size: {}", ibanNameCheckResponseEntities.size());

        List<AccountNameCheckData> accountNameCheckData = ibanNameCheckResponseEntities.stream()
                .map(entity -> {
                    AccountNameCheckData data = new AccountNameCheckData();
                    data.setCounterPartyAccount(entity.getCounterPartyAccount());
                    data.setCounterPartyName(entity.getCounterPartyName());
                    data.setFinalResult(FinalResult.valueOf(entity.getMatchingResult()));
                    data.setStatus(FinalStatus.valueOf(entity.getAccountStatus()));
                    data.setAccountHolderType(entity.getAccountHolderType());

                    return data;
                }).collect(Collectors.toList());

        log.info("accountNameCheckData size: {}", accountNameCheckData.size());

        return accountNameCheckData;
    }



}
