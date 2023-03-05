package com.sparkle.demo.ibannamecheckasyncimpl.web.util;

import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FileUtil {

    public Mono<String> getRealMimeType(InputStream inputStream) {
        return Mono.fromCallable(() -> {
            AutoDetectParser parser = new AutoDetectParser();
            Detector detector = parser.getDetector();
            try {
                Metadata metadata = new Metadata();
                TikaInputStream stream = TikaInputStream.get(inputStream);
                MediaType mediaType = detector.detect(stream, metadata);
                return mediaType.toString();
            } catch (IOException e) {
                return MimeTypes.OCTET_STREAM;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
