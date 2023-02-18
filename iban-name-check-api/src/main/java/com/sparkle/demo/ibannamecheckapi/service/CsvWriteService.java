package com.sparkle.demo.ibannamecheckapi.service;

import com.sparkle.demo.ibannamecheckapi.web.model.response.IbanAccountCheckResponse;
import com.sparkle.demo.ibannamecheckcommon.model.utils.ByteArrayInOutStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Slf4j
@Service
public class CsvWriteService {

    public Mono<ByteArrayInputStream> generateCsvResponse(IbanAccountCheckResponse ibanNameCheckResponse) {
        final CSVFormat format = CSVFormat
                .RFC4180
                .withQuoteMode(QuoteMode.ALL)
                .withHeader("Iban", "Naam", "Resultaat", "Info", "Naam Suggestie", "Status", "AccountHolderType")
                .builder()
                .build();

        return Mono.fromCallable(() -> {
            try {
                ByteArrayInOutStream stream = new ByteArrayInOutStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
                CSVPrinter csvPrinter = new CSVPrinter(streamWriter, format);
                ibanNameCheckResponse.getBatchResponse()
                    .forEach(response -> {
                        try {
                            csvPrinter.printRecord(
                                response.getResult().getAccount().getIban(),
                                response.getResult().getAccountHolderName(),
                                response.getResult().getResultType().name(),
                                "Info",
                                response.getResult().getSuggestedName(),
                                response.getResult().getAccount().getStatus().name(),
                                response.getResult().getAccount().getAccountHolderType().name());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                csvPrinter.flush();
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
