package com.sparkle.demo.ibannamecheckapi.init;

import com.sparkle.demo.ibannamecheckapi.document.IbanNameDocument;
import com.sparkle.demo.ibannamecheckapi.repository.IbanNameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitService implements CommandLineRunner {

    private final IbanNameRepository ibanNameRepository;

    @Override
    public void run(String... args) {

        IbanNameDocument ibanNameDocument1 = IbanNameDocument.create(
                null,
                "NL11RABO1234567890",
                "IBAN",
                "Daan Jeroen Maarten",
                "D.J.M.",
                "+31635408638");

        IbanNameDocument ibanNameDocument2 = IbanNameDocument.create(
                null,
                "NL91ABNA0417164300",
                "IBAN",
                "Maartejn Maayer",
                "M.M.",
                "+31897408554");

        Flux.just(ibanNameDocument1, ibanNameDocument2)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext((document) -> ibanNameRepository.deleteAll().subscribe())
                .flatMap(this.ibanNameRepository::save)
                .subscribe(mongoDoc -> log.info("inserted document(s) : {}", mongoDoc));
    }
}
