package com.sparkle.demo.ibannamecheckasyncimpl.business;

import com.sparkle.demo.ibannamecheckasyncimpl.service.excel.ExcelFileService;
import com.sparkle.demo.ibannamecheckasyncimpl.service.pain.PainFileService;
import com.sparkle.demo.ibannamecheckasyncimpl.web.model.request.IbanNameModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IbanNameCheckBusinessImpl {

    private final PainFileService painFileService;
    private final ExcelFileService excelFileService;

    public Mono<ByteArrayInputStream> uploadPainFile(Mono<FilePart> filePartMono) {
        return this.painFileService.processPainFile(filePartMono);
    }

    public Flux<List<IbanNameModel>> uploadExcelFileAsFlux(Flux<FilePart> filePartFlux) {
        return excelFileService.processExcelFileAsFlux(filePartFlux);
    }

    public Mono<List<IbanNameModel>> uploadExcelFileAsMono(Mono<FilePart> filePartMono) {
        return excelFileService.processExcelFileAsMono(filePartMono);
    }
}
