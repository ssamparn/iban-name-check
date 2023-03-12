package com.sparkle.demo.ibannamecheckasyncimpl.web.service;

import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.TaskIdRequest;
import com.sparkle.demo.ibannamecheckcommon.model.utils.ByteArrayInOutStream;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Slf4j
@Service
public class CsvWriteService {

    enum RequestCsvHeaders {
        IBAN, NAME, TRANSACTION_ID
    }

    public Mono<ByteArrayInputStream> createCsvRequest(List<IbanNameModel> ibanNameModels) {
        final CSVFormat format = CSVFormat.RFC4180.withHeader(RequestCsvHeaders.class);
        return Mono.fromCallable(() -> {
            try {
                ByteArrayInOutStream stream = new ByteArrayInOutStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
                CSVPrinter csvPrinter = new CSVPrinter(streamWriter, format);

                csvPrinter.printRecords(ibanNameModels);
                csvPrinter.flush();
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    enum TaskIdRequestCsvHeaders {
        IBAN, NAME, TRANSACTION_ID
    }

    public Mono<ByteArrayInputStream> createFirstCsvRequest(TaskIdRequest request) {
        final CSVFormat format = CSVFormat.RFC4180.withHeader(TaskIdRequestCsvHeaders.class);
        return Mono.fromCallable(() -> {
            try {
                ByteArrayInOutStream stream = new ByteArrayInOutStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
                CSVPrinter csvPrinter = new CSVPrinter(streamWriter, format);

                csvPrinter.printRecords(request.getFirstRequestList());
                csvPrinter.flush();
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
