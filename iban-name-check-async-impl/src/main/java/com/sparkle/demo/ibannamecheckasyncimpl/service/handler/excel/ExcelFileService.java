package com.sparkle.demo.ibannamecheckasyncimpl.service.handler.excel;

import com.sparkle.demo.ibannamecheckasyncimpl.service.mapper.FileMapper;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFileService {

    private final FileMapper fileMapper;

    public Mono<List<IbanNameModel>> uploadExcelFileAsMono(Mono<FilePart> filePartMono) {
        return filePartMono
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<List<IbanNameModel>> uploadExcelFileAsFlux(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .map(fileMapper::getFilePartRequestAsInputStream)
                .map(fileMapper::excelToIbanNameModel)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
