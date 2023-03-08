package com.sparkle.demo.ibannamecheckasyncimpl.database.config;

import com.sparkle.demo.ibannamecheckasyncimpl.database.entity.IbanNameEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.database.repository.IbanNameRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@Configuration
public class R2DbcConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    @Bean
    public CommandLineRunner demo(IbanNameRepository repository) {

        Flux<IbanNameEntity> ibanNameEntityFlux = Flux.just(
                new IbanNameEntity(UUID.randomUUID(), "Jack 1", "IBAN RABO 1"),
                new IbanNameEntity(UUID.randomUUID(),"Jack 2", "IBAN AMRO 1"),
                new IbanNameEntity(UUID.randomUUID(),"Jack 3", "IBAN ABN 1")
        );

        return (args) ->
                repository.deleteAll()
                        .thenMany(ibanNameEntityFlux)
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(repository::save)
                        .subscribe(entity -> log.info("inserted document(s) : {}", entity));
    }
}
