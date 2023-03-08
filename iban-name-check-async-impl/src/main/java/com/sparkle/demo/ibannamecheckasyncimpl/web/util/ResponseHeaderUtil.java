package com.sparkle.demo.ibannamecheckasyncimpl.web.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseHeaderUtil {

    private static String fileName = String.format("%s.xlsx", RandomStringUtils.randomAlphabetic(10));

    public static HttpHeaders excelResponseHeaders(String correlationId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        // responseHeaders.setContentType(MediaType.parseMediaType("application/vnd.ms-excel")); // MimeType for .xls
        responseHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + fileName);
        responseHeaders.add("correlationId", correlationId);

        return responseHeaders;
    }

    public static HttpHeaders jsonResponseHeaders(String correlationId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("correlationId", correlationId);

        return responseHeaders;
    }
}
