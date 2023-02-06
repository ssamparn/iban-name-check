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
                "NL86RABO6333227641",
                "IBAN",
                "Sally Snozcumber",
                "S.S.M.",
                "+31356406218");

        IbanNameDocument ibanNameDocument2 = IbanNameDocument.create(
                null,
                "NL06ABNA5558304928",
                "IBAN",
                "Roy Olsson",
                "R.O.",
                "+31897458054");

        IbanNameDocument ibanNameDocument3 = IbanNameDocument.create(
                null,
                "NL36INGB2682297498",
                "IBAN",
                "Barry Grey",
                "B.G.M.",
                "+31639468638");

        IbanNameDocument ibanNameDocument4 = IbanNameDocument.create(
                null,
                "NL10RABO9837080566",
                "IBAN",
                "Chloe Donaldson",
                "C.D.",
                "+31397308555");

        IbanNameDocument ibanNameDocument5 = IbanNameDocument.create(
                null,
                "NL89INGB6034837898",
                "IBAN",
                "Alison Blackman",
                "A.B",
                "+31635498638");

        IbanNameDocument ibanNameDocument6 = IbanNameDocument.create(
                null,
                "NL57ABNA2454554658",
                "IBAN",
                "Gemma Parkes",
                "G.P.",
                "+31807408354");

        IbanNameDocument ibanNameDocument7 = IbanNameDocument.create(
                null,
                "NL86INGB4110487447",
                "IBAN",
                "Suzanne Blast",
                "S.B",
                "+31635408698");

        IbanNameDocument ibanNameDocument8 = IbanNameDocument.create(
                null,
                "NL75ABNA9372718300",
                "IBAN",
                "Sally Lakeman",
                "S.L.",
                "+31897408054");

        IbanNameDocument ibanNameDocument9 = IbanNameDocument.create(
                null,
                "NL23RABO5299017782",
                "IBAN",
                "Hannah Connor",
                "H.C",
                "+31635408630");

        IbanNameDocument ibanNameDocument10 = IbanNameDocument.create(
                null,
                "NL22ABNA5206019070",
                "IBAN",
                "Morwenna Zeus",
                "M.Z.",
                "+31897408556");

        ibanNameRepository.deleteAll()
            .thenMany(
                Flux.just(
                    ibanNameDocument1,
                    ibanNameDocument2,
                    ibanNameDocument3,
                    ibanNameDocument4,
                    ibanNameDocument5,
                    ibanNameDocument6,
                    ibanNameDocument7,
                    ibanNameDocument8,
                    ibanNameDocument9,
                    ibanNameDocument10
                )
                .publishOn(Schedulers.boundedElastic()) // If we introduce publishOn, we can make this code more performant, so that the Flux don't block each other:
                .flatMap(this.ibanNameRepository::save) // https://spring.io/blog/2019/12/13/flight-of-the-flux-3-hopping-threads-and-schedulers
            )
            .subscribe(mongoDoc -> log.info("inserted document(s) : {}", mongoDoc));
    }
}
