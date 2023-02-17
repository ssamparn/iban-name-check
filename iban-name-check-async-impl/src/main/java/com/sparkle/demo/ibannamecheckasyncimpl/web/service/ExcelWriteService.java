package com.sparkle.demo.ibannamecheckasyncimpl.web.service;

import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.BulkResponse;
import com.sparkle.demo.ibannamecheckcommon.model.surepay.response.IbanNameCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ExcelWriteService {

    public Mono<ByteArrayInputStream> writeToExcel(IbanNameCheckResponse bulkResponses) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        return Mono.fromCallable(() -> {
            try(Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Iban Name Check Response");
                CellStyle cellStyle = workbook.createCellStyle();
                //set border to table
                cellStyle.setBorderTop(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                // Header
                Row row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("Id");
                cell.setCellStyle(cellStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue("Name");
                cell1.setCellStyle(cellStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue("Role");
                cell2.setCellStyle(cellStyle);

                //Set data
                int rowNum = 1;
                for (BulkResponse response : bulkResponses.getBatchResponse()) {
                    Row empDataRow = sheet.createRow(rowNum++);
                    Cell empIdCell = empDataRow.createCell(0);
                    empIdCell.setCellStyle(cellStyle);
                    empIdCell.setCellValue(response.getResult().getSuggestedName());

                    Cell empNameCell = empDataRow.createCell(1);
                    empNameCell.setCellStyle(cellStyle);
                    empNameCell.setCellValue(response.getResult().getAccountResult().getIban());

                    Cell empRoleCell = empDataRow.createCell(2);
                    empRoleCell.setCellStyle(cellStyle);
                    empRoleCell.setCellValue(response.getResult().getAccountResult().getAccountStatus().name());
                }
                workbook.write(stream);
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
