package com.sparkle.demo.ibannamecheckasyncimpl.service.handler.pain;

import com.sparkle.demo.ibannamecheckasyncimpl.client.IbanNameCheckClient;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.ResultType;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import com.sparkle.demo.ibannamecheckasyncimpl.service.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.IbanNameCheckData;
import com.sparkle.demo.ibannamecheckasyncimpl.web.service.CsvWriterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PainFileService {

    private final FileMapper fileMapper;
    private final IbanNameCheckClient ibanNameCheckClient;
    private final CsvWriterService csvWriterService;

    public Mono<ByteArrayInputStream> uploadFile(Mono<FilePart> filePart) {
        return filePart
                .map(this::getFilePartRequestAsInputStream)
                .map(inputStream -> (PipedInputStream) inputStream)
                .flatMap(this::readContentFromPipedInputStream)
                .flatMap(fileMapper::mapToRootDocument)
                .map(fileMapper::mapToSurePayRequest)
                .flatMap(ibanNameCheckClient::doPost)
                .map(this::mapToIbanNameCheckData)
                .flatMap(csvWriterService::generateCsv)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<IbanNameCheckData> mapToIbanNameCheckData(IbanNameCheckResponse surePayResponse) {
        List<IbanNameCheckData> ibanNameCheckDataList =  surePayResponse.getBatchResponse().stream()
            .map(bulkResponse -> {
                IbanNameCheckData ibanNameCheckData = new IbanNameCheckData();
                ibanNameCheckData.setAccount(bulkResponse.getResult().getAccountResult().getIban());
                ibanNameCheckData.setStatus(bulkResponse.getResult().getAccountResult().getAccountStatus().name());
                ibanNameCheckData.setMatched(bulkResponse.getResult().getResultType() == ResultType.MATCHING);
                return ibanNameCheckData;
            }).collect(Collectors.toList());

        log.info("mapped ibanNameCheckData size : {}", ibanNameCheckDataList.size());
        log.info("mapped ibanNameCheckData : {}", ibanNameCheckDataList);
        return ibanNameCheckDataList;
    }

    public InputStream getFilePartRequestAsInputStream(FilePart filePart) {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 10 * 20);
        try {
            pipedInputStream.connect(pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Flux<DataBuffer> body = filePart.content();
        DataBufferUtils.write(body, pipedOutputStream)
            .log("Writing to output buffer")
            .subscribe();

        return pipedInputStream;
    }

    private Mono<String> readContentFromPipedInputStream(PipedInputStream stream) {
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
