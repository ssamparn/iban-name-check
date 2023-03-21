package com.sparkle.demo.ibannamecheckasyncimpl.web.service;

import com.sparkle.demo.ibannamecheckasyncimpl.web.model.response.AccountNameCheckData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ExcelWriteService {

    private static final String SHEET = "Iban Name Check Response";

    private static final String[] SECOND_ROW_HEADERS = { "Iban", "Naam", " ", "Resultaat", "Info*", "NaamSuggestie", "Status", "AccountHolderType" };

    public Mono<ByteArrayInputStream> writeToExcel(List<AccountNameCheckData> ibanNames) {

        return Mono.fromCallable(() -> {

            try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream stream = new ByteArrayOutputStream()) {

                Sheet sheet = workbook.createSheet(SHEET);
                CellStyle cellStyle = workbook.createCellStyle();

                //set border to table
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);

                // first header row
                createFirstHeaderRow(sheet);

                // Second Header Row
                Row secondRow = sheet.createRow(1);

                // Second row header Names
                for (int col = 0; col < SECOND_ROW_HEADERS.length; col++) {
                    log.info("Writing second row headers to excel");
                    Cell cell = secondRow.createCell(col);
                    cell.setCellValue(SECOND_ROW_HEADERS[col]);
                    cell.setCellStyle(cellStyle);
                }

                //Set data
                int rowNum = 2;
                for (AccountNameCheckData nameCheckData : ibanNames) {
                    log.info("Writing data to excel");
                    Row ibanNameRow = sheet.createRow(rowNum++);
                    Cell counterPartyAccountNumCell = ibanNameRow.createCell(0);
                    counterPartyAccountNumCell.setCellStyle(cellStyle);
                    counterPartyAccountNumCell.setCellValue(nameCheckData.getCounterPartyAccount());

                    Cell counterPartyNameCell = ibanNameRow.createCell(1);
                    counterPartyNameCell.setCellStyle(cellStyle);
                    counterPartyNameCell.setCellValue(nameCheckData.getCounterPartyName());

                    Cell resultTypeCell = ibanNameRow.createCell(3);
                    resultTypeCell.setCellStyle(cellStyle);
                    resultTypeCell.setCellValue(nameCheckData.getStatus().name());

                    Cell infoCell = ibanNameRow.createCell(4);
                    infoCell.setCellStyle(cellStyle);
                    infoCell.setCellValue(nameCheckData.getSwitchedToIban());

                    Cell suggestedNameCell = ibanNameRow.createCell(5);
                    suggestedNameCell.setCellStyle(cellStyle);
                    suggestedNameCell.setCellValue(nameCheckData.getSwitchingServiceStatus());

                    Cell statusCell = ibanNameRow.createCell(6);
                    statusCell.setCellStyle(cellStyle);
                    statusCell.setCellValue(nameCheckData.getStatus().name());

                    Cell accountHolderTypeCell = ibanNameRow.createCell(7);
                    accountHolderTypeCell.setCellStyle(cellStyle);
                    accountHolderTypeCell.setCellValue(nameCheckData.getAccountHolderType());

                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 7));
                workbook.write(stream);
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private void createFirstHeaderRow(Sheet sheet) {
        // First Header Row
        Row firstRow = sheet.createRow(0);

        log.info("Writing first row headers to excel");
        Cell firstRowFirstCell = firstRow.createCell(0);
        Cell firstRowSecondCell = firstRow.createCell(3);

        firstRowFirstCell.setCellValue("Gegevens uit het eigen bestand");
        createFirstHeaderRowCellStyle(firstRowFirstCell);

        firstRowSecondCell.setCellValue("Resultaat uit Iban-Naam Controle");
        createFirstHeaderRowCellStyle(firstRowSecondCell);
    }

    private void createFirstHeaderRowCellStyle(Cell firstRowCell) {
        CellStyle firstRowCellStyle = firstRowCell.getCellStyle();
        if(firstRowCellStyle == null) {
            firstRowCellStyle = firstRowCell.getSheet().getWorkbook().createCellStyle();
        }
        firstRowCellStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        firstRowCellStyle.setBorderTop(BorderStyle.THIN);
        firstRowCellStyle.setBorderRight(BorderStyle.THIN);
        firstRowCellStyle.setBorderBottom(BorderStyle.THIN);
        firstRowCellStyle.setBorderLeft(BorderStyle.THIN);
        firstRowCellStyle.setAlignment(HorizontalAlignment.LEFT);
    }
}
