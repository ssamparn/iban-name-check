package com.sparkle.demo.ibannamecheckasyncimpl.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class IbanNameCheckClientConfig {

    @Value("${service.url.sure-pay}")
    private String baseUrl;

    @Bean("jsonWebClient")
    public WebClient jsonWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(jsonHttpClient()))
                .build();
    }

    private HttpClient jsonHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10)));
    }

    @Bean("csvWebClient")
    public WebClient csvWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(csvHttpClient()))
                .codecs(csvCodecConfigurer -> {
                    csvCodecConfigurer.customCodecs().register(csvFilePartReader());
                })
                .build();
    }

    public DefaultPartHttpMessageReader csvFilePartReader() {
        return new DefaultPartHttpMessageReader();
    }


    private HttpClient csvHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10)));
    }
}
