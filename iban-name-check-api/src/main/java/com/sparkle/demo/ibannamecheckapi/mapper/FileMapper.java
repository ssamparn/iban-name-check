package com.sparkle.demo.ibannamecheckapi.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

@Slf4j
@Service
public class FileMapper {

    public InputStream getFilePartRequestAsInputStream(FilePart filePart) {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 10 * 20);
        try {
            pipedInputStream.connect(pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Flux<DataBuffer> dataBufferFlux = filePart.content();
        DataBufferUtils.write(dataBufferFlux, pipedOutputStream)
                .log("Writing to output buffer")
                .subscribe();

        return pipedInputStream;
    }

    public Mono<String> readContentFromPipedInputStream(PipedInputStream stream) {
        StringBuffer contentStringBuffer = new StringBuffer();
        try {
            Thread pipeReader = new Thread(() -> {
                try {
                    contentStringBuffer.append(readContent(stream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pipeReader.start();
            pipeReader.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Mono.just(String.valueOf(contentStringBuffer));
    }

    private String readContent(InputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        byte[] tmp = new byte[stream.available()];
        int byteCount = stream.read(tmp, 0, tmp.length);
        log.info(String.format("read %d bytes from the stream\n", byteCount));
        contentStringBuffer.append(new String(tmp));
        return String.valueOf(contentStringBuffer);
    }
}
