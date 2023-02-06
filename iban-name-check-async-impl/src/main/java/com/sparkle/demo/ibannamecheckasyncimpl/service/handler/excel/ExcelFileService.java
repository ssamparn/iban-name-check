package com.sparkle.demo.ibannamecheckasyncimpl.service.handler.excel;

import com.sparkle.demo.ibannamecheckasyncimpl.utils.MultipartFileUploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

    public Flux<String> uploadFile(Flux<FilePart> fp) {
        return fp.flatMap(filePart ->
                filePart.content().map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    log.info(new String(bytes, StandardCharsets.UTF_8));
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .map(this::processAndGetLinesAsList)
                .flatMapIterable(Function.identity()));
    }

    private List<String> processAndGetLinesAsList(String string) {

        Supplier<Stream<String>> streamSupplier = string::lines;
        var isFileOk = streamSupplier.get().allMatch(line -> line.matches(MultipartFileUploadUtils.REGEX_RULES));

        return isFileOk ? streamSupplier.get().filter(s -> !s.isBlank()).collect(Collectors.toList()) : new ArrayList<>();
    }
}
