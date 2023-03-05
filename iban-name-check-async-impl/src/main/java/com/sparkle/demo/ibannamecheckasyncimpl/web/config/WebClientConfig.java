package com.sparkle.demo.ibannamecheckasyncimpl.web.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${service.url.sure-pay}")
    private String baseUrl;

    @Bean("jsonWebClient")
    public WebClient jsonWebClient(SslContext sslContext) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(jsonHttpClient(sslContext)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
                .build();
    }

    @Bean
    public HttpClient jsonHttpClient(SslContext sslContext) {
        return HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .wiretap(true)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10)));
    }

    @Bean("csvWebClient")
    public WebClient csvWebClient(SslContext sslContext) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(csvHttpClient(sslContext)))
                .codecs(csvCodecConfigurer -> {
                    csvCodecConfigurer.customCodecs().register(csvFilePartReader());
                })
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
                .build();
    }

    private DefaultPartHttpMessageReader csvFilePartReader() {
        return new DefaultPartHttpMessageReader();
    }

    @Bean
    public HttpClient csvHttpClient(SslContext sslContext) {
        return HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .wiretap(true)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10)));
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            log.info("--- Http Headers: ---");
            clientRequest.headers().forEach(this::logHeader);
            log.info("--- Http Cookies: ---");
            clientRequest.cookies().forEach(this::logHeader);
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }

    private void logHeader(String name, List<String> values) {
        values.forEach(value -> log.info("{}={}", name, value));
    }
}
