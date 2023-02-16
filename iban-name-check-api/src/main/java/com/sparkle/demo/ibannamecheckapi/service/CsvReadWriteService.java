package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.document.IbanNameDocument;
import com.sparkle.demo.ibannamecheckapi.web.model.response.BulkResponse;
import com.sparkle.demo.ibannamecheckcommon.model.utils.ByteArrayInOutStream;
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
public class CsvReadWriteService {

    enum ResponseCsvHeaders {
        ACCOUNT, STATUS, ISMATCHED
    }

    public Mono<ByteArrayInputStream> generateCsvResponse(List<BulkResponse> bulkResponses) {
        final CSVFormat format = CSVFormat.RFC4180.withHeader(ResponseCsvHeaders.class);
        return Mono.fromCallable(() -> {
            try {
                ByteArrayInOutStream stream = new ByteArrayInOutStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
                CSVPrinter csvPrinter = new CSVPrinter(streamWriter, format);

                csvPrinter.printRecords(bulkResponses);
                csvPrinter.flush();
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
